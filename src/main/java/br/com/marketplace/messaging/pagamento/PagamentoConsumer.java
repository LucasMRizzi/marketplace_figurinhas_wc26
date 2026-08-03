package br.com.marketplace.messaging.pagamento;

import br.com.marketplace.config.RabbitConfig;
import br.com.marketplace.entity.Concretizacao;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Venda;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.payment.PagamentoGateway;
import br.com.marketplace.payment.ResultadoPagamento;
import br.com.marketplace.repository.ConcretizacaoRepository;
import br.com.marketplace.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PagamentoConsumer {

    private final ConcretizacaoRepository concretizacaoRepository;
    private final VendaRepository vendaRepository;
    private final PagamentoGateway pagamentoGateway;

    @RabbitListener(
            queues = RabbitConfig.PROCESSAR_PAGAMENTO_QUEUE
    )
    @Transactional
    public void processar(
            ProcessarPagamentoMessage mensagem
    ) {
        Concretizacao concretizacao = concretizacaoRepository
                .findById(mensagem.idConcretizacao())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Concretização não encontrada."
                        )
                );

        if (!concretizacao.estaPendente()) {
            System.out.println(
                    "Concretização ignorada. Status: "
                            + concretizacao.getStatusPagamento()
            );
            return;
        }

        Oferta oferta = concretizacao.getOferta();

        if (!oferta.estaPendente()) {
            return;
        }

        Venda venda = vendaRepository
                .findById(oferta.getIdOferta())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Venda não encontrada."
                        )
                );

        concretizacao.iniciarPagamento();

        ResultadoPagamento resultado =
                pagamentoGateway.processar(
                        venda.getValorDaProposta(),
                        concretizacao.getAceitante().getCpf(),
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
    }
}