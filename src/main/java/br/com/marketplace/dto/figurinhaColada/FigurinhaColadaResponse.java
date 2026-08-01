package br.com.marketplace.dto.figurinhaColada;

import br.com.marketplace.entity.enums.TipoFigurinha;

public record FigurinhaColadaResponse (
        String nomeAlbum,
        String cpfUsuario,
        String codigoFigurinha,
        TipoFigurinha tipoFigurinha,
        String nomeFigurinha
) {
}
