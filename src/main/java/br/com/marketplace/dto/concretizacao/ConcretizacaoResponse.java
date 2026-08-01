package br.com.marketplace.dto.concretizacao;

import br.com.marketplace.entity.enums.StatusPagamento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConcretizacaoResponse(
        Integer idConcretizacao,
        Integer idOferta,
        StatusPagamento statusPagamento,
        LocalDate dataAceite,
        String cpfAceitante,
        String nomeAceitante,
        String cpfProponente
) {
}