package br.com.marketplace.dto.desejaFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarDesejaFigurinhaRequest(

        @NotBlank(message = "O CPF do aceitante é obrigatório.")
        @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres.")
        String cpfUsuario,

        @NotBlank(message = "O código da figurinha é obrigatório.")
        @Size(max = 6, message = "O código da figurinha deve ter no máximo 6 caracteres.")
        String codigoFigurinha,

        @NotNull(message = "O tipo da figurinha é obrigatório.")
        TipoFigurinha tipoFigurinha
) {

}

/** Exemplo de arquivo json:
 * {
  "cpfUsuario": "123.456.789-00",
  "codigoFigurinha": "BRA10",
  "tipoFigurinha": "BRILHANTE"
  }
 */