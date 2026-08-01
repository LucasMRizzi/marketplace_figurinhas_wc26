package br.com.marketplace.dto.avaliacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AvaliacaoResponse(
        Integer idConcretizacao,
        String cpfAvaliador,
        String nomeAvaliador,
        String cpfAvaliado,
        String nomeAvaliado,
        BigDecimal nota,
        String comentario,
        LocalDate data
) {
}