package br.com.marketplace.repository;

import br.com.marketplace.entity.PropostaTroca;
import br.com.marketplace.entity.enums.StatusPropostaTroca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropostaTrocaRepository extends JpaRepository<PropostaTroca, Integer> {
    boolean existsByOfertaIdOfertaAndSolicitanteCpfAndStatus(
            Integer idOferta, String cpf, StatusPropostaTroca status);
    List<PropostaTroca> findByOfertaUsuarioProponenteCpfOrderByDataCriacaoDesc(String cpf);
    List<PropostaTroca> findBySolicitanteCpfOrderByDataCriacaoDesc(String cpf);
    List<PropostaTroca> findByOfertaUsuarioProponenteCpfAndStatusOrderByDataCriacaoDesc(
            String cpf, StatusPropostaTroca status);
    List<PropostaTroca> findBySolicitanteCpfAndStatusOrderByDataCriacaoDesc(
            String cpf, StatusPropostaTroca status);
    List<PropostaTroca> findByOfertaIdOfertaAndStatus(
            Integer idOferta, StatusPropostaTroca status);
}
