package br.com.marketplace.dto.troca;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AtualizarTrocaRequest (

        @NotNull(message = "O prazo limite é obrigatório.")
        @FutureOrPresent(message = "A data deve ser hoje ou uma data futura.")
        LocalDate prazoLimite,

        @NotNull(message = "A descrição é obrigatória mesmo que esteja em branco.")
        String descricao
) {
}
