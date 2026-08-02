package br.com.marketplace.service;

import br.com.marketplace.dto.concretizacao.ConcretizacaoResponse;
import br.com.marketplace.dto.concretizacao.CriarConcretizacaoRequest;
import br.com.marketplace.entity.Concretizacao;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.Venda;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.exception.RegraDeNegocioException;
import br.com.marketplace.mapper.ConcretizacaoMapper;
import br.com.marketplace.messaging.pagamento.PagamentoSolicitadoEvent;
import br.com.marketplace.payment.PagamentoGateway;
import br.com.marketplace.payment.ResultadoPagamento;
import br.com.marketplace.repository.ConcretizacaoRepository;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import br.com.marketplace.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por gerenciar a finalização (concretização) das negociações.
 * Representa o momento em que um usuário (aceitante) concorda com os termos de uma 
 * oferta (venda ou troca) criada por outro usuário (proponente).
 */
@Service
@RequiredArgsConstructor
public class ConcretizacaoService {

    private final ConcretizacaoRepository concretizacaoRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConcretizacaoMapper concretizacaoMapper;

    /**
     * Registra o aceite de uma oferta, gerando uma nova concretização no sistema.
     * Realiza diversas validações de negócio para garantir que a oferta está elegível 
     * e que o aceitante é válido (não permitindo, por exemplo, o aceite da própria oferta).
     *
     * @param idOferta Identificador da oferta que está sendo aceita.
     * @param request  Objeto contendo os dados da requisição, como o CPF do usuário aceitante.
     * @return ConcretizacaoResponse com os dados do acordo firmado.
     * @throws RecursoNaoEncontradoException Se a oferta ou o usuário aceitante não existirem no banco.
     * @throws RegraDeNegocioException       Se a oferta não estiver com status PENDENTE ou se o proponente tentar aceitar a própria oferta.
     * @throws RecursoJaExisteException      Se já existir uma concretização registrada para esta mesma oferta.
     */
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ConcretizacaoResponse criar(
            Integer idOferta,
            CriarConcretizacaoRequest request
    ) {
        Oferta oferta = ofertaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Oferta não encontrada."
                        )
                );

        if (!oferta.estaPendente()) {
            throw new RegraDeNegocioException(
                    "Apenas ofertas pendentes podem ser aceitas."
            );
        }

        if (concretizacaoRepository
                .existsByOfertaIdOferta(idOferta)) {
            throw new RecursoJaExisteException(
                    "Essa oferta já possui uma concretização."
            );
        }

        Usuario aceitante = usuarioRepository
                .findById(request.cpfAceitante())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário aceitante não encontrado."
                        )
                );

        if (oferta.getUsuarioProponente()
                .getCpf()
                .equals(aceitante.getCpf())) {
            throw new RegraDeNegocioException(
                    "O proponente não pode aceitar a própria oferta."
            );
        }

        Concretizacao concretizacao =
                new Concretizacao(
                        oferta,
                        aceitante
                );

        Concretizacao salva =
                concretizacaoRepository.save(concretizacao);

        /*
         * Garante que o ID seja gerado antes de criar o evento.
         */
        concretizacaoRepository.flush();

        eventPublisher.publishEvent(
                new PagamentoSolicitadoEvent(
                        salva.getIdConcretizacao()
                )
        );

        return concretizacaoMapper.toResponse(salva);
    }

    /**
     * Recupera os detalhes de uma concretização específica pelo seu ID.
     *
     * @param idConcretizacao Identificador único da concretização.
     * @return ConcretizacaoResponse com os dados formatados.
     * @throws RecursoNaoEncontradoException Se o ID não for encontrado no banco de dados.
     */
    @Transactional(readOnly = true)
    public ConcretizacaoResponse buscar(
            Integer idConcretizacao
    ) {
        return concretizacaoMapper.toResponse(
                buscarEntidade(idConcretizacao)
        );
    }

    /**
     * Busca os dados da concretização associada a uma oferta específica.
     * Útil para rastrear quem aceitou uma determinada oferta após ela sair do status PENDENTE.
     *
     * @param idOferta ID da oferta vinculada à concretização.
     * @return ConcretizacaoResponse com os dados da negociação finalizada.
     * @throws RecursoNaoEncontradoException Se a oferta informada ainda não possuir uma concretização.
     */
    @Transactional(readOnly = true)
    public ConcretizacaoResponse buscarPorOferta(
            Integer idOferta
    ) {
        Concretizacao concretizacao =
                concretizacaoRepository
                        .findByOfertaIdOferta(idOferta)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Concretização não encontrada."
                                )
                        );

        return concretizacaoMapper.toResponse(concretizacao);
    }

    /**
     * Retorna um histórico global com todas as concretizações registradas no sistema.
     *
     * @return Lista de ConcretizacaoResponse.
     */
    @Transactional(readOnly = true)
    public List<ConcretizacaoResponse> listarTodas() {
        return concretizacaoRepository.findAll()
                .stream()
                .map(concretizacaoMapper::toResponse)
                .toList();
    }

    /**
     * Retorna a lista de todas as ofertas que foram aceitas por um usuário específico.
     *
     * @param cpf CPF do usuário aceitante.
     * @return Lista de ConcretizacaoResponse vinculadas ao usuário como aceitante.
     */
    @Transactional(readOnly = true)
    public List<ConcretizacaoResponse> listarPorAceitante(
            String cpf
    ) {
        return concretizacaoRepository
                .findByAceitanteCpf(cpf)
                .stream()
                .map(concretizacaoMapper::toResponse)
                .toList();
    }

    /**
     * Método utilitário privado para centralizar a busca por uma entidade Concretizacao
     * e padronizar o lançamento da exceção caso ela não exista.
     *
     * @param idConcretizacao ID da concretização a ser buscada.
     * @return Entidade Concretizacao bruta mapeada do banco de dados.
     * @throws RecursoNaoEncontradoException Se a concretização não for encontrada.
     */
    /*
     * =========================================================
     * Buscas Auxiliares
     * =========================================================
     */

    private Concretizacao buscarEntidade(
            Integer idConcretizacao
    ) {
        return concretizacaoRepository
                .findById(idConcretizacao)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Concretização não encontrada."
                        )
                );
    }
}