package br.com.marketplace.payment;

import java.math.BigDecimal;

public interface PagamentoGateway {

    ResultadoPagamento processar(
            BigDecimal valor,
            String cpfPagador,
            String cpfRecebedor
    );
}
