package br.com.marketplace.repository;

import br.com.marketplace.entity.ItemOfertado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemOfertadoRepository
        extends JpaRepository<ItemOfertado, Integer> {

    @Query("""
            SELECT item
            FROM ItemOfertado item
            WHERE item.oferta.idOferta = :idOferta
            ORDER BY item.idItem
            """)
    List<ItemOfertado> buscarTodosPorOferta(
            @Param("idOferta") Integer idOferta
    );
}