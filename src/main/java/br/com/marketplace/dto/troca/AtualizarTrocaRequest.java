package br.com.marketplace.dto.troca;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AtualizarTrocaRequest (

        @NotNull(message = "O prazo limite é obrigatório.")
        @FutureOrPresent(message = "A data deve ser hoje ou uma data futura.")
        LocalDateTime prazoLimite,

        @NotNull(message = "A descrição é obrigatória mesmo que esteja em branco.")
        String descricao
) {
}

/** Exemplo de arquivo json:
 * {
  "prazoLimite": "2026-08-15T23:59:59",
  "descricao": "Nova descrição: Aceito jogadores de outras seleções europeias também."
  }
 */