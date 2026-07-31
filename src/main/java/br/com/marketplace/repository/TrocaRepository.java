package br.com.marketplace.repository;

import br.com.marketplace.entity.Troca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrocaRepository extends JpaRepository<Troca, Integer> {

    List<Troca> findByOfertaUsuarioProponenteCpf(
            String cpf
    );
}