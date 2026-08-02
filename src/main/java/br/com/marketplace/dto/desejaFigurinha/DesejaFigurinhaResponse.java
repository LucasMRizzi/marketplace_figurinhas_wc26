package br.com.marketplace.dto.desejaFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record DesejaFigurinhaResponse(
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha
) {
}

/** Exemplo de arquivo json:
 * {
  "cpfUsuario": "123.456.789-00",
  "codigoFigurinha": "BRA10",
  "tipoFigurinha": "BRILHANTE",
  "nomeFigurinha": "Vinícius Júnior"
  }
 */