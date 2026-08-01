package br.com.marketplace.dto.troca;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CriarTrocaRequest(

        @NotNull(message = "O prazo limite é obrigatório.")
        @FutureOrPresent(message = "A data deve ser hoje ou uma data futura.")
        LocalDate prazoLimite,

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
