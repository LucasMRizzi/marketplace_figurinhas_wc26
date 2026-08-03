package br.com.marketplace.service;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import br.com.marketplace.dto.troca.AtualizarTrocaRequest;
import br.com.marketplace.dto.troca.CriarTrocaRequest;
import br.com.marketplace.dto.troca.TrocaResponse;
import br.com.marketplace.entity.*;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.entity.id.FigurinhaId;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.exception.RegraDeNegocioException;
import br.com.marketplace.mapper.TrocaMapper;
import br.com.marketplace.repository.*;
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
    private final PosseFigurinhaRepository posseFigurinhaRepository;
    private final FigurinhaRepository figurinhaRepository;
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
                proponente,
                request.prazoLimite(),
                request.descricao()
        );

        adicionarItensOfertados(
                oferta,
                proponente,
                request.itensOfertados()
        );

        Troca troca = trocaMapper.toEntity(
                request,
                oferta
        );

        adicionarItensSolicitados(
                troca,
                request.itensSolicitados()
        );

        oferta.calcularValorDeMercado();

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        return trocaMapper.toResponse(
                ofertaSalva.getTroca()
        );
    }

    /**
     * Atualiza os dados editáveis de uma proposta de troca em andamento.
     *
     * @param idOferta Identificador único da oferta vinculada à troca.
     * @param request  Objeto contendo os novos dados (prazo limite e descrição).
     * @throws RecursoNaoEncontradoException Se a troca não for encontrada para atualização.
     */
    @Transactional
    public TrocaResponse atualizar(
            Integer idOferta,
            AtualizarTrocaRequest request
    ) {
        Troca troca = buscarEntidade(idOferta);

        troca.getOferta().atualizarOferta(
                request.prazoLimite(),
                request.descricao()
        );

        return trocaMapper.toResponse(troca);
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

        if (!troca.getOferta().estaPendente()) {
            throw new RegraDeNegocioException(
                    "Apenas trocas pendentes podem ser removidas."
            );
        }

        ofertaRepository.delete(
                troca.getOferta()
        );
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

    /**
     * TODO
     */
    private void adicionarItensOfertados(
            Oferta oferta,
            Usuario proponente,
            List<CriarItemOfertadoRequest> itens
    ) {
        for (CriarItemOfertadoRequest itemRequest : itens) {

            PosseFigurinha posse = posseFigurinhaRepository
                    .findById(itemRequest.idPosse())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Posse de ID "
                                            + itemRequest.idPosse()
                                            + " não encontrada."
                            )
                    );

            validarPosseDoProponente(
                    posse,
                    proponente
            );

            ItemOfertado item = new ItemOfertado(
                    oferta,
                    posse,
                    itemRequest.quantidadeOfertada(),
                    itemRequest.condicao(),
                    itemRequest.foto()
            );

            oferta.adicionarItemOfertado(item);
        }
    }

    /**
     * TODO
     */
    private void adicionarItensSolicitados(
            Troca troca,
            List<CriarItemSolicitadoRequest> itens
    ) {
        for (CriarItemSolicitadoRequest itemRequest : itens) {

            FigurinhaId figurinhaId = new FigurinhaId(
                    itemRequest.codigoFigurinha(),
                    itemRequest.tipoFigurinha()
            );

            Figurinha figurinha = figurinhaRepository
                    .findById(figurinhaId)
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Figurinha "
                                            + itemRequest.codigoFigurinha()
                                            + " do tipo "
                                            + itemRequest.tipoFigurinha()
                                            + " não encontrada."
                            )
                    );

            ItemSolicitado item = new ItemSolicitado(
                    troca,
                    figurinha,
                    itemRequest.quantidade()
            );

            troca.adicionarItemSolicitado(item);
        }
    }

    /**
     * =========================================================
     * Validações Auxiliares
     * =========================================================
     */

    private void validarPosseDoProponente(
            PosseFigurinha posse,
            Usuario proponente
    ) {
        if (!posse.getUsuario()
                .getCpf()
                .equals(proponente.getCpf())) {

            throw new RegraDeNegocioException(
                    "A posse informada não pertence ao proponente da troca."
            );
        }
    }

    /**
     * =========================================================
     * Buscas Auxiliares
     * =========================================================
     */

    private Usuario buscarUsuario(String cpf) {
        return usuarioRepository.findById(cpf)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }

    private Troca buscarEntidade(Integer idOferta) {
        return trocaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Troca não encontrada."
                        )
                );
    }

    private Oferta buscarOferta (Integer idOferta){
        return ofertaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Oferta não encontrada."
                        )
                );
    }
}