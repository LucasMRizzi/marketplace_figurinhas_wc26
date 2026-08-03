package br.com.marketplace.repository;

import br.com.marketplace.entity.Concretizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcretizacaoRepository
        extends JpaRepository<Concretizacao, Integer> {

    Optional<Concretizacao> findByOfertaIdOferta(
            Integer idOferta
    );

    boolean existsByOfertaIdOferta(
            Integer idOferta
    );

    List<Concretizacao> findByAceitanteCpf(
            String cpf
    );
}