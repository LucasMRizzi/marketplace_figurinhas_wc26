package br.com.marketplace.dto.posseFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;
import java.math.BigDecimal;

public record PosseFigurinhaResponse (
        Integer idPosse,
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha,
        Integer quantidade,
        BigDecimal valorDeMercado
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
