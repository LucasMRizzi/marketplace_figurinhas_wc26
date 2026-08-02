package br.com.marketplace.service;

import br.com.marketplace.dto.troca.AtualizarTrocaRequest;
import br.com.marketplace.dto.troca.CriarTrocaRequest;
import br.com.marketplace.dto.troca.TrocaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Troca;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.Venda;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.TrocaMapper;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.TrocaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por gerenciar as negociações do tipo Troca (escambo) no marketplace.
 * Atua em conjunto com a entidade base Oferta, garantindo que toda Troca criada 
 * possua uma Oferta matriz associada a ela.
 */
@Service
@RequiredArgsConstructor
public class TrocaService {

    private final TrocaRepository trocaRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TrocaMapper trocaMapper;

    /**
     * Cria uma nova proposta de troca no sistema. 
     * Este método realiza a inserção em duas etapas: primeiro gera a entidade matriz (Oferta) 
     * e, em seguida, cria e vincula a entidade específica (Troca) contendo o prazo e a descrição.
     *
     * @param cpfProponente CPF do usuário que está propondo a troca.
     * @param request       Objeto contendo os dados específicos da troca (prazo limite, descrição, etc.).
     * @return TrocaResponse com os dados da troca recém-criada.
     * @throws RecursoNaoEncontradoException Se o usuário proponente não existir no banco de dados.
     */
    @Transactional
    public TrocaResponse criar(
            String cpfProponente,
            CriarTrocaRequest request
    ) {
        Usuario proponente = buscarUsuario(cpfProponente);

        Oferta oferta = new Oferta(
                TipoOferta.TROCA,
                proponente
        );

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        Troca troca = new Troca(
                ofertaSalva,
                request.prazoLimite(),
                request.descricao()
        );

        Troca trocaSalva = trocaRepository.save(troca);

        return trocaMapper.toResponse(trocaSalva);
    }

    /**
     * Busca os detalhes de uma proposta de troca específica.
     *
     * @param idOferta Identificador único da oferta vinculada à troca.
     * @return TrocaResponse contendo os dados formatados.
     * @throws RecursoNaoEncontradoException Se não existir uma troca associada ao ID informado.
     */
    @Transactional(readOnly = true)
    public TrocaResponse buscar(Integer idOferta) {
        return trocaMapper.toResponse(
                buscarEntidade(idOferta)
        );
    }

    /**
     * Retorna um histórico com todas as propostas de troca registradas no marketplace.
     *
     * @return Lista contendo todas as TrocaResponse.
     */
    @Transactional(readOnly = true)
    public List<TrocaResponse> listarTodas() {
        return trocaRepository.findAll()
                .stream()
                .map(trocaMapper::toResponse)
                .toList();
    }

    /**
     * Lista todas as propostas de troca criadas por um usuário específico.
     *
     * @param cpf CPF do usuário proponente.
     * @return Lista de TrocaResponse vinculadas a este usuário.
     */
    @Transactional(readOnly = true)
    public List<TrocaResponse> listarPorProponente(
            String cpf
    ) {
        return trocaRepository
                .findByOfertaUsuarioProponenteCpf(cpf)
                .stream()
                .map(trocaMapper::toResponse)
                .toList();
    }

    /**
     * Atualiza os dados editáveis de uma proposta de troca em andamento.
     *
     * @param idOferta Identificador único da oferta vinculada à troca.
     * @param request  Objeto contendo os novos dados (prazo limite e descrição).
     * @throws RecursoNaoEncontradoException Se a troca não for encontrada para atualização.
     */
    @Transactional
    public void atualizar(
            Integer idOferta,
            AtualizarTrocaRequest request
    ) {
        Troca troca = buscarEntidade(idOferta);

        troca.atualizar(
                request.prazoLimite(),
                request.descricao()
        );
    }

    /**
     * Remove uma proposta de troca do sistema. 
     * A exclusão é feita deletando a entidade matriz (Oferta), o que deve disparar a 
     * exclusão em cascata (CascadeType.ALL / orphanRemoval) da Troca associada no banco de dados.
     *
     * @param idOferta Identificador da oferta vinculada à troca que será deletada.
     * @throws RecursoNaoEncontradoException Se a troca não for localizada.
     */
    @Transactional
    public void remover(Integer idOferta) {
        Troca troca = buscarEntidade(idOferta);

        ofertaRepository.delete(
                troca.getOferta()
        );
    }

    /**
     * Método utilitário privado para centralizar a busca por uma entidade Troca
     * e padronizar o lançamento da exceção.
     *
     * @param idOferta Identificador único da oferta associada.
     * @return A entidade Troca encontrada.
     * @throws RecursoNaoEncontradoException Se o ID não corresponder a nenhuma troca.
     */
    private Troca buscarEntidade(Integer idOferta) {
        return trocaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Troca não encontrada."
                        )
                );
    }

    /**
     * Método utilitário privado para verificar e instanciar o proponente da troca.
     *
     * @param cpf CPF do usuário.
     * @return A entidade Usuario encontrada no banco.
     * @throws RecursoNaoEncontradoException Se o usuário não existir.
     */
    private Usuario buscarUsuario(String cpf) {
        return usuarioRepository.findById(cpf)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }
}