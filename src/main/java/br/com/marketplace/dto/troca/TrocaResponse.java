package br.com.marketplace.dto.troca;

import br.com.marketplace.entity.enums.StatusOferta;

import java.time.LocalDate;

public record TrocaResponse(
        Integer idOferta,
        StatusOferta status,
        LocalDate dataCriacao,
        String cpfProponente,
        LocalDate prazoLimite,
        String descricao
) {
}
