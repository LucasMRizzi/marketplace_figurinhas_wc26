package br.com.marketplace.repository;

import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public interface OfertaRepository extends JpaRepository<Oferta, Integer>, JpaSpecificationExecutor<Oferta> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Oferta o where o.idOferta = :idOferta")
    Optional<Oferta> buscarParaAtualizacao(@Param("idOferta") Integer idOferta);

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

    List<Oferta> findByStatusAndPrazoLimiteBefore(
                StatusOferta status,
                LocalDateTime dataAtual
    );
}
