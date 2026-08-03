package br.com.marketplace.dto.venda;

import br.com.marketplace.entity.enums.StatusOferta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaResponse(

        Integer idOferta,
        StatusOferta status,
        LocalDateTime dataCriacao,
        LocalDateTime prazoLimite,
        String descricao,
        BigDecimal valorDeMercado,
        String cpfProponente,
        BigDecimal valorDaProposta
) {
}

/** Exemplo de arquivo json:
 * {
  "idOferta": 46,
  "status": "PENDENTE",
  "dataCriacao": "2026-08-01T11:00:00",
  "prazoLimite": "2026-08-25T23:59:59",
  "descricao": "Baixei o preço pra vender rápido!",
  "valorDeMercado": 190.50,
  "cpfProponente": "123.456.789-00",
  "valorDaProposta": 180.00
  }
 */