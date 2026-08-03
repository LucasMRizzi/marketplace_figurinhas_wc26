package br.com.marketplace.repository;

import br.com.marketplace.entity.PosseFigurinha;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.id.FigurinhaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PosseFigurinhaRepository extends JpaRepository<PosseFigurinha, Integer> {
    Optional<PosseFigurinha>
    findByUsuarioCpfAndFigurinhaIdCodigoAndFigurinhaIdTipo(
            String cpf,
            String codigo,
            TipoFigurinha tipo
    );

    boolean
    existsByUsuarioCpfAndFigurinhaIdCodigoAndFigurinhaIdTipo(
            String cpf,
            String codigo,
            TipoFigurinha tipo
    );

    List<PosseFigurinha> findByUsuarioCpf(String cpf);

    boolean
    existsByUsuarioCpfAndFigurinhaId(
            String cpf,
            FigurinhaId figurinhaId
    );
}
