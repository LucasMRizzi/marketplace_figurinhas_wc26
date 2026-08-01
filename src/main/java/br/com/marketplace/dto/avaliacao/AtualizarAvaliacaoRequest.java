package br.com.marketplace.dto.avaliacao;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarAvaliacaoRequest(

        @NotNull(message = "A nota é obrigatória.")
        @DecimalMin(value = "0.00", message = "A nota mínima é zero.")
        @DecimalMax(value = "5.00", message = "A nota máxima é 5.")
        BigDecimal nota,

        @NotNull(message = "O comentário não pode ser nulo.")
        @Size(max = 150, message = "O comentário não pode ser maior que 150 caracteres.")
        String comentario
) {
}