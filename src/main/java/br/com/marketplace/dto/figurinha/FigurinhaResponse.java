package br.com.marketplace.dto.figurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;

import java.math.BigDecimal;

public record FigurinhaResponse(
        String codigo,
        TipoFigurinha tipo,
        String nome,
        BigDecimal valorDeMercado
) {
}


/** Exemplo de arquivo json:
 * {
  "codigo": "ARG10",
  "tipo": "NORMAL",
  "nome": "Lionel Messi (Legend)",
  "valorDeMercado": 75.50
  }
 */