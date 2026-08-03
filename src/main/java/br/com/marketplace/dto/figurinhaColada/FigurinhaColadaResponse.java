package br.com.marketplace.dto.figurinhaColada;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record FigurinhaColadaResponse (
        String nomeAlbum,
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha
) {
}

/** Exemplo de arquivo json:
 * {
  "nomeAlbum": "Copa do Mundo 2026",
  "cpfUsuario": "123.456.789-00",
  "codigoFigurinha": "BRA10",
  "tipoFigurinha": "BRILHANTE",
  "nomeFigurinha": "Vinícius Júnior"
  }
 */