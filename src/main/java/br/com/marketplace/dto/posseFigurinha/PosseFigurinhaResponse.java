package br.com.marketplace.dto.posseFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record PosseFigurinhaResponse (
        Integer idPosse,
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        Integer quantidade
) {
}

/** Exemplo de arquivo json:
 * {
  "idPosse": 88,
  "cpfUsuario": "123.456.789-00",
  "codigoFigurinha": "POR07",
  "tipoFigurinha": "NORMAL",
  "quantidade": 2
  }
 */