package br.com.marketplace.dto.itemsolicitado;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record ItemSolicitadoResponse(
        Long idItemSolicitado,
        Long idOferta,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha,
        Integer quantidade
) {
}