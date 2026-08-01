package br.com.marketplace.controller;

import br.com.marketplace.dto.avaliacao.AtualizarAvaliacaoRequest;
import br.com.marketplace.dto.avaliacao.AvaliacaoResponse;
import br.com.marketplace.dto.avaliacao.CriarAvaliacaoRequest;
import br.com.marketplace.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping("/concretizacoes/{idConcretizacao}")
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponse criar(
            @PathVariable Integer idConcretizacao,
            @Valid
            @RequestBody CriarAvaliacaoRequest request
    ) {
        return avaliacaoService.criar(
                idConcretizacao,
                request
        );
    }

    @GetMapping(
            "/{idConcretizacao}/{cpfAvaliador}/{cpfAvaliado}"
    )
    public AvaliacaoResponse buscar(
            @PathVariable Integer idConcretizacao,
            @PathVariable String cpfAvaliador,
            @PathVariable String cpfAvaliado
    ) {
        return avaliacaoService.buscar(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao
        );
    }

    @GetMapping("/avaliados/{cpf}")
    public List<AvaliacaoResponse> listarPorAvaliado(
            @PathVariable String cpf
    ) {
        return avaliacaoService
                .listarPorAvaliado(cpf);
    }

    @GetMapping("/avaliadores/{cpf}")
    public List<AvaliacaoResponse> listarPorAvaliador(
            @PathVariable String cpf
    ) {
        return avaliacaoService
                .listarPorAvaliador(cpf);
    }

    @PutMapping(
            "/{idConcretizacao}/{cpfAvaliador}/{cpfAvaliado}"
    )
    public AvaliacaoResponse atualizar(
            @PathVariable Integer idConcretizacao,
            @PathVariable String cpfAvaliador,
            @PathVariable String cpfAvaliado,
            @Valid
            @RequestBody AtualizarAvaliacaoRequest request
    ) {
        return avaliacaoService.atualizar(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao,
                request
        );
    }

    @DeleteMapping(
            "/{idConcretizacao}/{cpfAvaliador}/{cpfAvaliado}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable Integer idConcretizacao,
            @PathVariable String cpfAvaliador,
            @PathVariable String cpfAvaliado
    ) {
        avaliacaoService.remover(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao
        );
    }
}