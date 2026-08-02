package br.com.marketplace.messaging.pagamento;

import br.com.marketplace.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void solicitarProcessamento(
            Integer idConcretizacao
    ) {
        ProcessarPagamentoMessage mensagem =
                new ProcessarPagamentoMessage(
                        idConcretizacao
                );

        rabbitTemplate.convertAndSend(
                RabbitConfig.PAGAMENTO_EXCHANGE,
                RabbitConfig.PROCESSAR_PAGAMENTO_ROUTING_KEY,
                mensagem
        );
    }
}