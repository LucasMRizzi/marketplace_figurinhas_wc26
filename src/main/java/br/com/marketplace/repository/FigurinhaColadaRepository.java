package br.com.marketplace.repository;

import br.com.marketplace.entity.FigurinhaColada;
import br.com.marketplace.entity.id.FigurinhaColadaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FigurinhaColadaRepository extends JpaRepository<FigurinhaColada, FigurinhaColadaId> {
    List<FigurinhaColada>
    findByAlbum_Id_NomeAndAlbum_Id_Usuario(
            String nomeAlbum,
            String usuario
    );

    long countByAlbum_Id_NomeAndAlbum_Id_Usuario(
            String nomeAlbum,
            String cpfUsuario
    );
}
