package br.com.marketplace.payment.grupoDoisBank;

import br.com.marketplace.payment.PagamentoGateway;
import br.com.marketplace.payment.ResultadoPagamento;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class GrupoDoisBankPagamentoGateway implements PagamentoGateway {

    private static final BigDecimal LIMITE_APROVACAO =
            new BigDecimal("1000.0");

    @Override
    public ResultadoPagamento processar(
            BigDecimal valor,
            String cpfPagador,
            String cpfRecebedor
    ) {
        if (valor == null
                || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return ResultadoPagamento.recusado(
                    "O valor do pagamento é inválido."
            );
        }

        if (cpfPagador.equals(cpfRecebedor)) {
            return ResultadoPagamento.recusado(
                    "O pagador e o recebedor não podem ser o mesmo usuário."
            );
        }

        if (valor.compareTo(LIMITE_APROVACAO) > 0) {
            return ResultadoPagamento.recusado(
                    "Pagamento recusado pelo gateway de testes."
            );
        }

        return ResultadoPagamento.aprovado(
                "FAKE-" + UUID.randomUUID()
        );
    }
}
