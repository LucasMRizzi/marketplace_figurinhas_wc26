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

/** Exemplo de arquivo json:
 * {
  "idOferta": 45,
  "status": "PENDENTE",
  "dataCriacao": "2026-08-01T10:00:00",
  "prazoLimite": "2026-08-15T23:59:59",
  "descricao": "Nova descrição: Aceito jogadores de outras seleções europeias também.",
  "valorDeMercado": 150.00,
  "cpfProponente": "123.456.789-00"
  }
 */