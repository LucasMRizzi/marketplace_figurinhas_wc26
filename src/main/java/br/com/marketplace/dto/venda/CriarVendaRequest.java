package br.com.marketplace.dto.venda;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CriarVendaRequest (

        @NotNull(message = "O preço unitário é obrigatório.")
        @DecimalMin(
                value = "0.01",
                message = "O preço deve ser maior que zero."
        )
        BigDecimal valorDaProposta,

        @NotNull(message = "O prazo limite é obrigatório.")
        @FutureOrPresent(
                message = "O prazo limite não pode estar no passado."
        )
        LocalDateTime prazoLimite,

        @Size(
                max = 140,
                message = "A descrição deve possuir no máximo 140 caracteres."
        )
        String descricao,

        @NotEmpty(
                message = "A venda deve possuir pelo menos um item ofertado."
        )
        List<@Valid CriarItemOfertadoRequest> itensOfertados
) {
}
