package br.com.marketplace.dto.avaliacao;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarAvaliacaoRequest(

        @NotNull(message = "A nota é obrigatória.")
        @DecimalMin("0.00")
        @DecimalMax("5.00")
        BigDecimal nota,

        @NotNull(message = "O comentário não pode ser nulo.")
        @Size(max = 150)
        String comentario
) {
}