package br.com.marketplace.dto.troca;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AtualizarTrocaRequest (
        @NotNull
        @FutureOrPresent(message = "A data deve ser hoje ou uma data futura.")
        LocalDate prazoLimite,

        String descricao
) {
}
