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
import br.com.marketplace.payment.PagamentoGateway;
import br.com.marketplace.payment.ResultadoPagamento;
import br.com.marketplace.repository.ConcretizacaoRepository;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import br.com.marketplace.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcretizacaoService {

    private final ConcretizacaoRepository concretizacaoRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConcretizacaoMapper concretizacaoMapper;
    private final VendaRepository vendaRepository;

    private final PagamentoGateway pagamentoGateway;

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
                concretizacaoMapper.toEntity(
                        oferta,
                        aceitante
                );

        if (oferta.ehTroca()) {
            concretizacao.iniciarPagamento();

            concretizacao.confirmarPagamento(
                    "TROCA-SEM-PAGAMENTO"
            );

            oferta.concretizar();

            Concretizacao salva =
                    concretizacaoRepository.save(concretizacao);

            return concretizacaoMapper.toResponse(salva);
        }

        Venda venda = vendaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Dados da venda não encontrados."
                        )
                );

        concretizacao.iniciarPagamento();

        ResultadoPagamento resultado =
                pagamentoGateway.processar(
                        venda.getValorDaProposta(),
                        aceitante.getCpf(),
                        oferta.getUsuarioProponente().getCpf()
                );

        if (resultado.aprovado()) {
            concretizacao.confirmarPagamento(
                    resultado.codigoTransacao()
            );

            oferta.concretizar();
        } else {
            concretizacao.recusarPagamento(
                    resultado.mensagem()
            );
        }

        Concretizacao salva =
                concretizacaoRepository.save(concretizacao);

        return concretizacaoMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public ConcretizacaoResponse buscar(
            Integer idConcretizacao
    ) {
        return concretizacaoMapper.toResponse(
                buscarEntidade(idConcretizacao)
        );
    }

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

    @Transactional(readOnly = true)
    public List<ConcretizacaoResponse> listarTodas() {
        return concretizacaoRepository.findAll()
                .stream()
                .map(concretizacaoMapper::toResponse)
                .toList();
    }

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