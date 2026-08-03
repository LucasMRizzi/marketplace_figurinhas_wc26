package br.com.marketplace.dto.avaliacao;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarAvaliacaoRequest(

        @NotBlank(message = "O CPF do avaliador é obrigatório.")
        @Size(max = 14)
        String cpfAvaliador,

        @NotNull(message = "A nota é obrigatória.")
        @DecimalMin(
                value = "0.00",
                message = "A nota mínima é zero."
        )
        @DecimalMax(
                value = "5.00",
                message = "A nota máxima é cinco."
        )
        BigDecimal nota,

        @NotNull(message = "O comentário não pode ser nulo.")
        @Size(
                max = 150,
                message = "O comentário deve ter no máximo 150 caracteres."
        )
        String comentario
) {
}

/** Exemplo de arquivo json:
 * {
  "cpfAvaliador": "111.222.333-44",
  "nota": 4.50,
  "comentario": "Ótimo usuário, a figurinha chegou em perfeito estado!"
  }
 */