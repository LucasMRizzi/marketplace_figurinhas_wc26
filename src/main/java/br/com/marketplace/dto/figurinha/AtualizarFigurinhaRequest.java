package br.com.marketplace.dto.figurinha;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarFigurinhaRequest(

        @NotBlank(message = "O nome da figurinha é obrigatório.")
        @Size(max = 100, message = "O nome da figurinha não deve ter mais de 100 caracteres.")
        String nome,

        @NotNull(message = "O valor de mercado é obrigatório.")
        @DecimalMin(
                value = "0.01",
                message = "O preço deve ser maior que zero."
        )
        BigDecimal valorDeMercado
) {
}

/** Exemplo de arquivo json:
 * {
  "nome": "Lionel Messi (Legend)",
  "valorDeMercado": 75.50
  }
 */
