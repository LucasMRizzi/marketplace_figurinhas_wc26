package br.com.marketplace.dto.troca;

import br.com.marketplace.entity.enums.StatusOferta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrocaResponse(

        Integer idOferta,
        StatusOferta status,
        LocalDateTime dataCriacao,
        LocalDateTime prazoLimite,
        String descricao,
        BigDecimal valorDeMercado,
        String cpfProponente
) {
}
