package br.com.marketplace.dto.figurinhaColada;

import br.com.marketplace.entity.enums.TipoFigurinha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarFigurinhaColadaRequest(

        @NotBlank(message = "O código da figurinha é obrigatório.")
        @Size(max = 6, message = "O código deve ter no máximo 6 caracteres.")
        String codigoFigurinha,

        @NotNull(message = "O tipo da figurinha é obrigatório.")
        TipoFigurinha tipoFigurinha
) {
}
