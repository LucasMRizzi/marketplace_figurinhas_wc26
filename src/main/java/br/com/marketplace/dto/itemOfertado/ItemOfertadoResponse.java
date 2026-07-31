package br.com.marketplace.dto.itemofertado;

import br.com.marketplace.entity.enums.Condicao;
import br.com.marketplace.entity.enums.TipoFigurinha;

public record ItemOfertadoResponse(
        Long idItem,
        Long idOferta,
        Long idPosse,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        Integer quantidadeOfertada,
        Condicao condicao,
        String foto
) {
}