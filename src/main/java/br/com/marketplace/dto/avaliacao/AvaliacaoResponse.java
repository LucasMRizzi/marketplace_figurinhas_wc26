package br.com.marketplace.dto.avaliacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

public record AvaliacaoResponse(
        Integer idConcretizacao,
        String cpfAvaliador,
        String nomeAvaliador,
        String cpfAvaliado,
        String nomeAvaliado,
        BigDecimal nota,
        String comentario,
        LocalDateTime data
) {
}