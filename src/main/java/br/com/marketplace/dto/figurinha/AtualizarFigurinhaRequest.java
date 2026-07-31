package br.com.marketplace.dto.figurinha;

import java.math.BigDecimal;

public record AtualizarFigurinhaRequest(
        String nome,
        BigDecimal valorDeMercado
) {
}
