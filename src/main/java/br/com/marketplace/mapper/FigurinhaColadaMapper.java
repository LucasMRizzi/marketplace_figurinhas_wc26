package br.com.marketplace.mapper;

import br.com.marketplace.dto.figurinhaColada.FigurinhaColadaResponse;
import br.com.marketplace.entity.Album;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.FigurinhaColada;
import org.springframework.stereotype.Component;

@Component
public class FigurinhaColadaMapper {

    public FigurinhaColada toEntity(
            Album album,
            Figurinha figurinha
    ) {
        return new FigurinhaColada(
                album,
                figurinha
        );
    }

    public FigurinhaColadaResponse toResponse(
            FigurinhaColada colada
    ) {
        return new FigurinhaColadaResponse(
                colada.getAlbum().getNome(),
                colada.getAlbum().getCpfUsuario(),
                colada.getFigurinha().getCodigo(),
                colada.getFigurinha().getTipo(),
                colada.getFigurinha().getNome()
        );
    }
}