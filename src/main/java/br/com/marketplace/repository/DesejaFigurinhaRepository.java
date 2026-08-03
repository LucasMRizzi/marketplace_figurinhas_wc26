package br.com.marketplace.repository;

import br.com.marketplace.entity.DesejaFigurinha;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.id.DesejaFigurinhaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesejaFigurinhaRepository extends JpaRepository<DesejaFigurinha, DesejaFigurinhaId> {
    List<DesejaFigurinha>
    findByFigurinhaIdCodigoAndFigurinhaIdTipo(
            String codigo,
            TipoFigurinha tipo
    );

    boolean
    existsByUsuarioCpfAndFigurinhaIdCodigoAndFigurinhaIdTipo(
            String cpf,
            String codigo,
            TipoFigurinha tipo
    );

    List<DesejaFigurinha> findByUsuarioCpf(String cpf);
}
