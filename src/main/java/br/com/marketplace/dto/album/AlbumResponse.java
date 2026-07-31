package br.com.marketplace.dto.album;

import java.math.BigDecimal;

public record AlbumResponse(
        String nome,
        String cpfUsuario,
        BigDecimal completude
) {
}
