package br.com.marketplace.dto.venda;

import br.com.marketplace.entity.enums.StatusOferta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaResponse(

        Integer idOferta,
        StatusOferta status,
        LocalDateTime dataCriacao,
        LocalDateTime prazoLimite,
        String descricao,
        BigDecimal valorDeMercado,
        String cpfProponente,
        BigDecimal valorDaProposta
) {
}
