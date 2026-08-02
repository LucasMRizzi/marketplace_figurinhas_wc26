package br.com.marketplace.dto.itemSolicitado;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record ItemSolicitadoResponse(
        Integer idItemSolicitado,
        Integer idOferta,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha,
        Integer quantidade
) {
}

/** Exemplo de arquivo json:
 * {
  "idItemSolicitado": 412,
  "idOferta": 45,
  "codigoFigurinha": "FRA07",
  "tipoFigurinha": "NORMAL",
  "nomeFigurinha": "Kylian Mbappé",
  "quantidade": 3
  }
 */