package br.com.marketplace.dto.itemSolicitado;

import br.com.marketplace.entity.enums.TipoFigurinha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CriarItemSolicitadoRequest(

        @NotBlank(message = "O código da figurinha é obrigatório.")
        @Size(
                max = 6,
                message = "O código deve possuir no máximo 6 caracteres."
        )
        String codigoFigurinha,

        @NotNull(message = "O tipo da figurinha é obrigatório.")
        TipoFigurinha tipoFigurinha,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {
}