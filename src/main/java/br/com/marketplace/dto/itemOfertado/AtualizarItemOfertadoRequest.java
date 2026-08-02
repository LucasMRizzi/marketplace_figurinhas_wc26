package br.com.marketplace.dto.itemOfertado;

import br.com.marketplace.entity.enums.Condicao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AtualizarItemOfertadoRequest(

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidadeOfertada,

        @NotNull(message = "A condição é obrigatória.")
        Condicao condicao,

        @Size(
                max = 255,
                message = "A foto deve possuir no máximo 255 caracteres."
        )
        String foto
) {
}

/** Exemplo de arquivo json:
 * {
  "quantidadeOfertada": 1,
  "condicao": "COM_MARCAS_DE_USO",
  "foto": "https://meubucket.com/fotos/figurinha-bra10-nova.jpg"
  }
 */