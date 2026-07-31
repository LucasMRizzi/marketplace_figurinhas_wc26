package br.com.marketplace.repository;

import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfertaRepository extends JpaRepository<Oferta, Integer> {
    List<Oferta> findByUsuarioProponenteCpf(
            String cpf
    );

    List<Oferta> findByTipo(
            TipoOferta tipo
    );

    List<Oferta> findByStatus(
            StatusOferta status
    );

    List<Oferta> findByTipoAndStatus(
            TipoOferta tipo,
            StatusOferta status
    );

    List<Oferta> findByUsuarioProponenteCpfAndTipo(
            String cpf,
            TipoOferta tipo
    );
}
