package br.com.marketplace.dto.oferta;

import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OfertaResponse (

        Integer idOferta,
        StatusOferta status,
        TipoOferta tipo,
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
  "tipo": "TROCA",
  "dataCriacao": "2026-08-01T10:00:00",
  "prazoLimite": "2026-08-10T23:59:59",
  "descricao": "Troco minhas repetidas do Brasil por jogadores da França.",
  "valorDeMercado": 150.00,
  "cpfProponente": "123.456.789-00"
  }
 */