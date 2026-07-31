package br.com.marketplace.dto.venda;

import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaResponse(
        Integer idOferta,
        StatusOferta status,
        TipoOferta tipo,
        LocalDate dataCriacao,
        String cpfProponente,
        BigDecimal valorDaProposta,
        BigDecimal precoUnitario,
        Integer quantidade
) {
}
