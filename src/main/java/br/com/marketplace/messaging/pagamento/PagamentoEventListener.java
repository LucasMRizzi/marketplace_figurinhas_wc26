package br.com.marketplace.messaging.pagamento;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PagamentoEventListener {

    private final PagamentoProducer pagamentoProducer;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void publicarPagamento(
            PagamentoSolicitadoEvent evento
    ) {
        pagamentoProducer.solicitarProcessamento(
                evento.idConcretizacao()
        );
    }
}