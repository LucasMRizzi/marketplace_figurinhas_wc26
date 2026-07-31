package br.com.marketplace.mapper;

import br.com.marketplace.dto.album.AlbumResponse;
import br.com.marketplace.dto.album.CriarAlbumRequest;
import br.com.marketplace.entity.Album;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.id.AlbumId;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper {

    public Album toEntity(
            CriarAlbumRequest request,
            Usuario usuario
    ){
        return new Album(
                request.nome(),
                usuario
        );
    }

    public AlbumResponse toResponse(
            Album album
    ){
        return new AlbumResponse(
            album.getId().getNome(),
            album.getUsuario().getCpf(),
            album.getCompletude()
        );
    }
}
