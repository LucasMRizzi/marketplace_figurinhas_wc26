package br.com.marketplace.dto.venda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarVendaRequest (
        @NotNull(message = "O preço unitário é obrigatório.")
        @DecimalMin(
                value = "0.01",
                message = "O preço deve ser maior que zero."
        )
        BigDecimal precoUnitario,

        Integer quantidade
) {
}
