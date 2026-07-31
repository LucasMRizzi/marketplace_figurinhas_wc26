package br.com.marketplace.controller;

import br.com.marketplace.dto.desejaFigurinha.CriarDesejaFigurinhaRequest;
import br.com.marketplace.dto.desejaFigurinha.DesejaFigurinhaResponse;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.service.DesejaFigurinhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{cpf}/desejos")
@RequiredArgsConstructor
public class DesejaFigurinhaController {

    private final DesejaFigurinhaService desejoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DesejaFigurinhaResponse adicionar(
            @PathVariable String cpf,
            @Valid
            @RequestBody CriarDesejaFigurinhaRequest request
    ) {
        return desejoService.adicionar(cpf, request);
    }

    @GetMapping
    public List<DesejaFigurinhaResponse> listar(
            @PathVariable String cpf
    ) {
        return desejoService.listarPorUsuario(cpf);
    }

    @GetMapping("/{codigo}/{tipo}")
    public DesejaFigurinhaResponse buscar(
            @PathVariable String cpf,
            @PathVariable String codigo,
            @PathVariable TipoFigurinha tipo
    ) {
        return desejoService.buscar(
                cpf,
                codigo,
                tipo
        );
    }

    @DeleteMapping("/{codigo}/{tipo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable String cpf,
            @PathVariable String codigo,
            @PathVariable TipoFigurinha tipo
    ) {
        desejoService.remover(
                cpf,
                codigo,
                tipo
        );
    }
}