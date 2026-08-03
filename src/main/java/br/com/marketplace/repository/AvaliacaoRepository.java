package br.com.marketplace.repository;

import br.com.marketplace.entity.Avaliacao;
import br.com.marketplace.entity.id.AvaliacaoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AvaliacaoRepository
        extends JpaRepository<Avaliacao, AvaliacaoId> {

    List<Avaliacao> findByUsuarioAvaliadoCpf(
            String cpf
    );

    List<Avaliacao> findByUsuarioAvaliadorCpf(
            String cpf
    );

    List<Avaliacao>
    findByConcretizacaoIdConcretizacao(
            Integer idConcretizacao
    );

    @Query("""
        SELECT AVG(a.nota)
        FROM Avaliacao a
        WHERE a.usuarioAvaliado.cpf = :cpf
    """)
    BigDecimal calcularMediaPorAvaliado(
            @Param("cpf") String cpf
    );
}