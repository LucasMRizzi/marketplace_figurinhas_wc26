package br.com.marketplace.dto.figurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarFigurinhaRequest(

        @NotBlank(message = "O código da figurinha é obrigatório.")
        @Size(max = 6, message = "O código da figurinha deve ter no máximo 6 caracteres.")
        String codigo,

        @NotNull(message = "O tipo da figurinha é obrigatório.")
        TipoFigurinha tipo,

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
  "codigo": "ARG10",
  "tipo": "NORMAL",
  "nome": "Lionel Messi",
  "valorDeMercado": 50.00
  }
 */