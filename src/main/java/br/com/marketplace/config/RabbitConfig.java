package br.com.marketplace.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PAGAMENTO_EXCHANGE =
            "marketplace.pagamentos.exchange";

    public static final String PROCESSAR_PAGAMENTO_QUEUE =
            "marketplace.pagamentos.processar";

    public static final String PROCESSAR_PAGAMENTO_ROUTING_KEY =
            "pagamento.processar";

    @Bean
    public DirectExchange pagamentoExchange() {
        return new DirectExchange(
                PAGAMENTO_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue processarPagamentoQueue() {
        return new Queue(
                PROCESSAR_PAGAMENTO_QUEUE,
                true
        );
    }

    @Bean
    public Binding processarPagamentoBinding(
            Queue processarPagamentoQueue,
            DirectExchange pagamentoExchange
    ) {
        return BindingBuilder
                .bind(processarPagamentoQueue)
                .to(pagamentoExchange)
                .with(PROCESSAR_PAGAMENTO_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}