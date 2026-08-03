package br.com.marketplace.repository;

import br.com.marketplace.entity.Album;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.id.AlbumId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, AlbumId> {
    List<Album> findByUsuarioCpf(String cpf);
}
