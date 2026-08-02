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

/** Exemplo de arquivo json:
 * {
  "idConcretizacao": 55,
  "idOferta": 204,
  "statusPagamento": "PENDENTE",
  "dataAceite": "2026-08-02T15:00:00",
  "cpfAceitante": "999.888.777-66",
  "nomeAceitante": "Carlos Mendes",
  "cpfProponente": "123.123.123-12"
  }
 */