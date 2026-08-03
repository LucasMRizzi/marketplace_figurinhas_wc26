package br.com.marketplace.dto.troca;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CriarTrocaRequest(

        @NotNull(message = "O prazo limite é obrigatório.")
        @FutureOrPresent(message = "A data deve ser hoje ou uma data futura.")
        LocalDateTime prazoLimite,

        @NotNull(message = "A descrição é obrigatória mesmo que esteja em branco.")
        String descricao,

        @NotEmpty(
                message = "A troca deve possuir pelo menos um item ofertado."
        )
        List<@Valid CriarItemOfertadoRequest> itensOfertados,

        @NotEmpty(
                message = "A troca deve possuir pelo menos um item solicitado."
        )
        List<@Valid CriarItemSolicitadoRequest> itensSolicitados
) {
}

/** Exemplo de arquivo json:
 * {
  "prazoLimite": "2026-08-10T23:59:59",
  "descricao": "Troco minhas repetidas do Brasil por jogadores da França.",
  "itensOfertados": [
    {
      "idPosse": 150,
      "quantidadeOfertada": 1,
      "condicao": "PERFEITA",
      "foto": "url-da-foto.jpg"
    }
  ],
  "itensSolicitados": [
    {
      "codigoFigurinha": "FRA07",
      "tipoFigurinha": "NORMAL",
      "quantidade": 1
    }
  ]
  }
 */