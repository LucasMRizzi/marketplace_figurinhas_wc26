package br.com.marketplace.dto.itemsolicitado;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AtualizarItemSolicitadoRequest(

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {
}