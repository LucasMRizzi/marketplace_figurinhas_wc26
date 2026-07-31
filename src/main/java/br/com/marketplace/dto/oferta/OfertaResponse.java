package br.com.marketplace.dto.oferta;

import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;

import java.time.LocalDate;

public record OfertaResponse (
    Integer idOferta,
    StatusOferta status,
    TipoOferta tipo,
    LocalDate dataCriacao,
    String cpfProponente
) {
}
