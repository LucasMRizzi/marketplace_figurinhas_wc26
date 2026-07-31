package br.com.marketplace.dto.posseFigurinha;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record PosseFigurinhaResponse (
        Integer idPosse,
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        Integer quantidade
) {
}
