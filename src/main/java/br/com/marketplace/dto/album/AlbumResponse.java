package br.com.marketplace.dto.album;

import java.math.BigDecimal;

public record AlbumResponse(
        String nome,
        String cpfUsuario,
        BigDecimal completude
) {
}

/** Exemplo de arquivo json:
 * {
  "nome": "Copa do Mundo 2026",
  "cpfUsuario": "123.456.789-00",
  "completude": 15.50
  }
 */