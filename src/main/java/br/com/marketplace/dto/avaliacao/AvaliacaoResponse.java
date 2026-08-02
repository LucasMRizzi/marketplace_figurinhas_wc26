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

/** Exemplo de arquivo json:
 * {
  "idConcretizacao": 102,
  "cpfAvaliador": "111.222.333-44",
  "nomeAvaliador": "João Silva",
  "cpfAvaliado": "555.666.777-88",
  "nomeAvaliado": "Maria Oliveira",
  "nota": 5.00,
  "comentario": "Atualizando a nota, o usuário me ajudou muito depois.",
  "data": "2026-08-02T14:30:00"
  }
 */