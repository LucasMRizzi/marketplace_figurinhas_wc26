package br.com.marketplace.dto.oferta;

import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OfertaResponse (

        Integer idOferta,
        StatusOferta status,
        TipoOferta tipo,
        LocalDateTime dataCriacao,
        LocalDateTime prazoLimite,
        String descricao,
        BigDecimal valorDeMercado,
        String cpfProponente
) {
}
