package br.com.marketplace.dto.concretizacao;

import br.com.marketplace.entity.enums.StatusPagamento;

import java.time.LocalDateTime;

public record ConcretizacaoResponse(
        Integer idConcretizacao,
        Integer idOferta,
        StatusPagamento statusPagamento,
        LocalDateTime dataAceite,
        String cpfAceitante,
        String nomeAceitante,
        String cpfProponente
) {
}