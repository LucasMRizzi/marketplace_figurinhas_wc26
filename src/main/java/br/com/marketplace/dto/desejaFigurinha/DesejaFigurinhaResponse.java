package br.com.marketplace.dto.desejaFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record DesejaFigurinhaResponse(
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha
) {
}
