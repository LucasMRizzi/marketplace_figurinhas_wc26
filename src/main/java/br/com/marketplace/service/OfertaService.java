package br.com.marketplace.service;

import br.com.marketplace.dto.oferta.OfertaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.OfertaMapper;
import br.com.marketplace.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Método utilitário privado para centralizar a busca por uma entidade Oferta
     * e padronizar o lançamento da exceção de recurso não encontrado.
     *
     * @param idOferta Identificador único da oferta.
     * @return Entidade Oferta bruta recuperada do banco de dados.
     * @throws RecursoNaoEncontradoException Se a oferta não existir.
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