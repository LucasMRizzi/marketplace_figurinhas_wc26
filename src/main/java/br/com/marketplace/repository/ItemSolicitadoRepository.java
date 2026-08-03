package br.com.marketplace.repository;

import br.com.marketplace.entity.ItemSolicitado;
import br.com.marketplace.entity.enums.TipoFigurinha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemSolicitadoRepository
        extends JpaRepository<ItemSolicitado, Integer> {

    @Query("""
            SELECT item
            FROM ItemSolicitado item
            WHERE item.troca.idOferta = :idOferta
            ORDER BY item.idItemSolicitado
            """)
    List<ItemSolicitado> buscarTodosPorTroca(
            @Param("idOferta") Integer idOferta
    );

    @Query("""
            SELECT COUNT(item) > 0
            FROM ItemSolicitado item
            WHERE item.troca.idOferta = :idOferta
              AND item.figurinha.id.codigo = :codigo
              AND item.figurinha.id.tipo = :tipo
            """)
    boolean existeFigurinhaNaTroca(
            @Param("idOferta") Integer idOferta,
            @Param("codigo") String codigo,
            @Param("tipo") TipoFigurinha tipo
    );
}