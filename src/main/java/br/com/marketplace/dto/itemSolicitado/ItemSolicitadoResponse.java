package br.com.marketplace.dto.itemSolicitado;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record ItemSolicitadoResponse(
        Integer idItemSolicitado,
        Integer idOferta,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha,
        Integer quantidade
) {
}