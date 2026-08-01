package br.com.marketplace.dto.itemOfertado;

import br.com.marketplace.entity.enums.Condicao;
import br.com.marketplace.entity.enums.TipoFigurinha;

public record ItemOfertadoResponse(
        Integer idItem,
        Integer idOferta,
        Integer idPosse,
        String nomeFigurinha,
        Integer quantidadeOfertada,
        Condicao condicao,
        String foto
) {
}