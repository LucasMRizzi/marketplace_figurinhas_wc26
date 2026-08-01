package br.com.marketplace.controller;

import br.com.marketplace.dto.concretizacao.ConcretizacaoResponse;
import br.com.marketplace.dto.concretizacao.CriarConcretizacaoRequest;
import br.com.marketplace.service.ConcretizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concretizacoes")
@RequiredArgsConstructor
public class ConcretizacaoController {

    private final ConcretizacaoService concretizacaoService;

    @PostMapping("/ofertas/{idOferta}")
    @ResponseStatus(HttpStatus.CREATED)
    public ConcretizacaoResponse criar(
            @PathVariable Integer idOferta,
            @Valid
            @RequestBody CriarConcretizacaoRequest request
    ) {
        return concretizacaoService.criar(
                idOferta,
                request
        );
    }

    @GetMapping
    public List<ConcretizacaoResponse> listarTodas() {
        return concretizacaoService.listarTodas();
    }

    @GetMapping("/{idConcretizacao}")
    public ConcretizacaoResponse buscar(
            @PathVariable Integer idConcretizacao
    ) {
        return concretizacaoService.buscar(
                idConcretizacao
        );
    }

    @GetMapping("/oferta/{idOferta}")
    public ConcretizacaoResponse buscarPorOferta(
            @PathVariable Integer idOferta
    ) {
        return concretizacaoService.buscarPorOferta(
                idOferta
        );
    }

    @GetMapping("/aceitante/{cpf}")
    public List<ConcretizacaoResponse> listarPorAceitante(
            @PathVariable String cpf
    ) {
        return concretizacaoService
                .listarPorAceitante(cpf);
    }
}