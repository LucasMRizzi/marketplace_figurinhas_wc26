package br.com.marketplace.repository;

import br.com.marketplace.entity.FigurinhaColada;
import br.com.marketplace.entity.id.FigurinhaColadaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FigurinhaColadaRepository extends JpaRepository<FigurinhaColada, FigurinhaColadaId> {
    List<FigurinhaColada>
    findByAlbumIdNomeAndAlbumIdUsuario(
            String nomeAlbum,
            String usuario
    );
}
