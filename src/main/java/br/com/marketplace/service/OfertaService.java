package br.com.marketplace.service;

import br.com.marketplace.dto.oferta.OfertaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.ItemOfertado;
import br.com.marketplace.entity.PosseFigurinha;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.OfertaMapper;
import br.com.marketplace.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável pelo gerenciamento principal das ofertas no marketplace.
 * Trata das consultas, filtros dinâmicos e de alterações manuais de estado (como a expiração),
 * servindo como base para as negociações de Venda e Troca.
 */
@Service
@RequiredArgsConstructor
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final OfertaMapper ofertaMapper;

    /**
     * Busca os detalhes de uma oferta específica pelo seu identificador.
     *
     * @param idOferta Identificador único da oferta.
     * @return OfertaResponse contendo os dados formatados da oferta.
     * @throws RecursoNaoEncontradoException Se a oferta não for encontrada no banco de dados.
     */
    @Transactional(readOnly = true)
    public OfertaResponse buscar(Integer idOferta) {
        return ofertaMapper.toResponse(
                buscarEntidade(idOferta)
        );
    }

    /**
     * Retorna uma lista de ofertas, permitindo filtragem dinâmica por tipo (Venda/Troca) 
     * e por status (Pendente, Concretizada, Expirada).
     * Se nenhum filtro for fornecido, retorna todas as ofertas registradas no sistema.
     *
     * @param tipo   Filtro opcional pelo tipo da oferta.
     * @param status Filtro opcional pelo status atual da oferta.
     * @return Lista de OfertaResponse correspondente aos filtros aplicados.
     */
    @Transactional(readOnly = true)
    public List<OfertaResponse> listar(
            TipoOferta tipo,
            StatusOferta status
    ) {
        List<Oferta> ofertas;

        if (tipo != null && status != null) {
            ofertas = ofertaRepository
                    .findByTipoAndStatus(tipo, status);
        } else if (tipo != null) {
            ofertas = ofertaRepository.findByTipo(tipo);
        } else if (status != null) {
            ofertas = ofertaRepository.findByStatus(status);
        } else {
            ofertas = ofertaRepository.findAll();
        }

        return ofertas.stream()
                .map(ofertaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfertaResponse> buscarAvancado(
            List<TipoOferta> tipos,
            List<TipoFigurinha> tiposFigurinha,
            BigDecimal valorMinimo,
            BigDecimal valorMaximo,
            String cidade,
            String termo,
            String cpfUsuarioExcluir
    ) {
        Specification<Oferta> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Oferta, Usuario> proponente = root.join("usuarioProponente", JoinType.INNER);

            if (tipos != null && !tipos.isEmpty()) {
                predicates.add(root.get("tipo").in(tipos));
            }
            predicates.add(builder.equal(root.get("status"), StatusOferta.PENDENTE));
            predicates.add(builder.notEqual(proponente.get("cpf"), cpfUsuarioExcluir));
            if (valorMinimo != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("valorDeMercado"), valorMinimo));
            }
            if (valorMaximo != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("valorDeMercado"), valorMaximo));
            }
            if (cidade != null && !cidade.isBlank()) {
                predicates.add(builder.equal(builder.lower(proponente.get("endereco").get("cidade")), cidade.trim().toLowerCase()));
            }
            if (termo != null && !termo.isBlank()) {
                String like = "%" + termo.trim().toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("descricao")), like),
                        builder.like(builder.lower(proponente.get("nome")), like),
                        builder.like(proponente.get("cpf"), like)
                ));
            }
            if (tiposFigurinha != null && !tiposFigurinha.isEmpty()) {
                Join<Oferta, ItemOfertado> item = root.join("itensOfertados", JoinType.INNER);
                Join<ItemOfertado, PosseFigurinha> posse = item.join("posseFigurinha", JoinType.INNER);
                Join<PosseFigurinha, Figurinha> figurinha = posse.join("figurinha", JoinType.INNER);
                predicates.add(figurinha.get("id").get("tipo").in(tiposFigurinha));
            }

            query.distinct(true);
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        return ofertaRepository.findAll(specification).stream()
                .map(ofertaMapper::toResponse)
                .toList();
    }

    /**
     * Retorna todo o histórico de ofertas criadas por um usuário específico (o proponente).
     *
     * @param cpf CPF do usuário proponente.
     * @return Lista de OfertaResponse vinculadas a este usuário.
     */
    @Transactional(readOnly = true)
    public List<OfertaResponse> listarPorUsuario(
            String cpf
    ) {
        return ofertaRepository
                .findByUsuarioProponenteCpf(cpf)
                .stream()
                .map(ofertaMapper::toResponse)
                .toList();
    }

    /**
     * Expira manualmente uma oferta, alterando seu status para EXPIRADA.
     * Utiliza o método encapsulado da entidade para garantir que apenas ofertas 
     * no status PENDENTE possam sofrer essa alteração.
     *
     * @param idOferta Identificador da oferta a ser expirada.
     * @return OfertaResponse com os dados atualizados da oferta.
     * @throws RecursoNaoEncontradoException Se a oferta não for encontrada.
     * @throws IllegalStateException         Se a oferta não estiver com o status PENDENTE (regra validada na entidade).
     */
    @Transactional
    public OfertaResponse expirar(Integer idOferta) {
        Oferta oferta = buscarEntidade(idOferta);

        oferta.expirar();

        return ofertaMapper.toResponse(oferta);
    }

    /**
     * =========================================================
     * Buscas Auxiliares
     * =========================================================
     */

    private Oferta buscarEntidade(Integer idOferta) {
        return ofertaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Oferta não encontrada."
                        )
                );
    }
}
