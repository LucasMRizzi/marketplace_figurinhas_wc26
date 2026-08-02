package br.com.marketplace.controller;

import br.com.marketplace.dto.figurinha.AtualizarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.CriarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.FigurinhaResponse;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.service.FigurinhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/figurinhas")
@RequiredArgsConstructor
public class FigurinhaController {

    private final FigurinhaService figurinhaService;

    /**
     * Criar nova figurinha
     *
     * POST http://localhost:8081/api/figurinhas
     * JSON RequestBody -> CriarFigurinhaRequest
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FigurinhaResponse criar(
            @Valid @RequestBody CriarFigurinhaRequest request
    ) {
        return figurinhaService.criar(request);
    }

    /**
     * Listar figurinhas
     *
     * POST http://localhost:8081/api/figurinhas
     */
    @GetMapping
    public List<FigurinhaResponse> listar(
            @RequestParam(required = false) String nome
    ) {
        if (nome != null && !nome.isBlank()) {
            return figurinhaService.buscarPorNome(nome);
        }

        return figurinhaService.listarTodos();
    }

    /**
     * Buscar figurinha
     *
     * POST http://localhost:8081/api/figurinhas/{codigo}/{tipo}
     */
    @GetMapping("/{codigo}/{tipo}")
    public FigurinhaResponse buscar(
            @PathVariable String codigo,
            @PathVariable TipoFigurinha tipo
    ) {
        return figurinhaService.buscar(codigo, tipo);
    }

    /**
     * Atualizar figurinha
     *
     * PUT http://localhost:8081/api/figurinhas{codigo}/{tipo}
     * JSON RequestBody -> AtualizarFigurinhaRequest
     */
    @PutMapping("/{codigo}/{tipo}")
    public FigurinhaResponse atualizar(
            @PathVariable String codigo,
            @PathVariable TipoFigurinha tipo,
            @Valid @RequestBody AtualizarFigurinhaRequest request
    ) {
        return figurinhaService.atualizar(
                codigo,
                tipo,
                request
        );
    }

    /**
     * Remover figurinha
     *
     * Remove http://localhost:8081/api/figurinhas{codigo}/{tipo}
     */
    @DeleteMapping("/{codigo}/{tipo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable String codigo,
            @PathVariable TipoFigurinha tipo
    ) {
        figurinhaService.remover(codigo, tipo);
    }
}
