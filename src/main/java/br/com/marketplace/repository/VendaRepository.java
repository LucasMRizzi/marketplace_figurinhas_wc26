package br.com.marketplace.repository;

import br.com.marketplace.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendaRepository
        extends JpaRepository<Venda, Integer> {

    List<Venda> findByOfertaUsuarioProponenteCpf(
            String cpf
    );
}
