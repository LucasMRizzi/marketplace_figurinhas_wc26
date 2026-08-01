package br.com.marketplace.dto.troca;

import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrocaResponse(

        Integer idOferta,
        StatusOferta status,
        LocalDate dataCriacao,
        LocalDate prazoLimite,
        String descricao,
        BigDecimal valorDeMercado,
        String cpfProponente
) {
}
