package br.com.marketplace.dto.venda;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AtualizarVendaRequest(

        @NotNull(message = "O novo valor da proposta é obrigatório.")
        @DecimalMin(
                value = "0.01",
                message = "O valor da proposta deve ser maior que zero."
        )
        BigDecimal valorDaProposta,

        @NotNull(message = "O prazo limite é obrigatório.")
        @FutureOrPresent(
                message = "O prazo limite não pode estar no passado."
        )
        LocalDateTime prazoLimite,

        @Size(
                max = 140,
                message = "A descrição deve possuir no máximo 140 caracteres."
        )
        String descricao
) {
}
