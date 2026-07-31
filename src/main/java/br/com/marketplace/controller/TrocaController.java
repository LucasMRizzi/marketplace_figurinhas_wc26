package br.com.marketplace.controller;

import br.com.marketplace.dto.troca.CriarTrocaRequest;
import br.com.marketplace.dto.troca.TrocaResponse;
import br.com.marketplace.service.TrocaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrocaController {

    private final TrocaService trocaService;

    @PostMapping("/api/usuarios/{cpf}/trocas")
    @ResponseStatus(HttpStatus.CREATED)
    public TrocaResponse criar(
            @PathVariable String cpf,
            @Valid @RequestBody CriarTrocaRequest request
    ) {
        return trocaService.criar(cpf, request);
    }

    @GetMapping("/api/trocas")
    public List<TrocaResponse> listarTodas() {
        return trocaService.listarTodas();
    }

    @GetMapping("/api/trocas/{idOferta}")
    public TrocaResponse buscar(
            @PathVariable Integer idOferta
    ) {
        return trocaService.buscar(idOferta);
    }

    @GetMapping("/api/usuarios/{cpf}/trocas")
    public List<TrocaResponse> listarPorProponente(
            @PathVariable String cpf
    ) {
        return trocaService.listarPorProponente(cpf);
    }

    @DeleteMapping("/api/trocas/{idOferta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable Integer idOferta
    ) {
        trocaService.remover(idOferta);
    }
}