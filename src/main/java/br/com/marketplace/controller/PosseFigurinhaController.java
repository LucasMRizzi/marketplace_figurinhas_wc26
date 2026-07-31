package br.com.marketplace.controller;

import br.com.marketplace.dto.posseFigurinha.CriarPosseFigurinhaRequest;
import br.com.marketplace.dto.posseFigurinha.PosseFigurinhaResponse;
import br.com.marketplace.service.PosseFigurinhaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{cpf}/posses")
@RequiredArgsConstructor
public class PosseFigurinhaController {

    private final PosseFigurinhaService posseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PosseFigurinhaResponse adicionar(
            @PathVariable String cpf,
            @Valid
            @RequestBody CriarPosseFigurinhaRequest request
    ) {
        return posseService.adicionar(cpf, request);
    }

    @GetMapping
    public List<PosseFigurinhaResponse> listar(
            @PathVariable String cpf
    ) {
        return posseService.listarPorUsuario(cpf);
    }

    @GetMapping("/{idPosse}")
    public PosseFigurinhaResponse buscar(
            @PathVariable String cpf,
            @PathVariable Integer idPosse
    ) {
        return posseService.buscar(idPosse);
    }

    @PatchMapping("/{idPosse}/quantidade/remover")
    public PosseFigurinhaResponse removerQuantidade(
            @PathVariable String cpf,
            @PathVariable Integer idPosse,
            @RequestParam @Positive int quantidade
    ) {
        return posseService.removerQuantidade(
                idPosse,
                quantidade
        );
    }

    @DeleteMapping("/{idPosse}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable String cpf,
            @PathVariable Integer idPosse
    ) {
        posseService.remover(idPosse);
    }
}
