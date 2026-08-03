package br.com.marketplace.mapper;

import br.com.marketplace.dto.desejaFigurinha.DesejaFigurinhaResponse;
import br.com.marketplace.entity.DesejaFigurinha;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class DesejaFigurinhaMapper {

    public DesejaFigurinha toEntity(
            Usuario usuario,
            Figurinha figurinha
    ) {
        return new DesejaFigurinha(
            usuario,
            figurinha
        );
    }

    public DesejaFigurinhaResponse toResponse(
            DesejaFigurinha desejaFigurinha
    ) {
        return new DesejaFigurinhaResponse(
                desejaFigurinha.getUsuario().getCpf(),
                desejaFigurinha.getFigurinha().getCodigo(),
                desejaFigurinha.getFigurinha().getTipo(),
                desejaFigurinha.getFigurinha().getNome()
        );
    }
}
