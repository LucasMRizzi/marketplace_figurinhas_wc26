package br.com.marketplace.dto.desejaFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record CriarDesejaFigurinhaRequest(
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha
) {

}
