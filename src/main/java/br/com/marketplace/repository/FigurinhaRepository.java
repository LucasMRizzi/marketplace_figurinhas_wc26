package br.com.marketplace.repository;

import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.id.FigurinhaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FigurinhaRepository extends JpaRepository<Figurinha, FigurinhaId> {

    List<Figurinha> findByIdTipo(TipoFigurinha tipo);

    Optional<Figurinha> findByNomeContainingIgnoreCase(String nome);
}
