package br.com.marketplace.dto.posseFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record CriarPosseFigurinhaRequest (
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        Integer quantidade
){
}
