package br.com.marketplace.controller;

import br.com.marketplace.dto.figurinha.AtualizarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.CriarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.FigurinhaResponse;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.service.FigurinhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/figurinhas")
@RequiredArgsConstructor
@Tag(
        name = "Catálogo de Figurinhas",
        description = "Gerenciamento do catálogo base e oficial de figurinhas do sistema."
)
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
    @Operation(
            summary = "Cadastrar nova figurinha oficial",
            description = "Insere uma nova figurinha no dicionário base do marketplace."
    )
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
    @Operation(
            summary = "Listar catálogo",
            description = "Retorna todas as figurinhas do sistema, oferecendo opção de busca por nome (fragmento de texto)."
    )
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
    @Operation(
            summary = "Buscar figurinha do catálogo",
            description = "Retorna os detalhes oficiais e preço base de mercado de uma figurinha."
    )
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
    @Operation(
            summary = "Atualizar informações da figurinha",
            description = "Altera o nome ou valor de mercado de uma figurinha existente no catálogo oficial."
    )
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
    @Operation(
            summary = "Remover figurinha",
            description = "Exclui permanentemente uma figurinha do dicionário oficial do sistema."
    )
    public void remover(
            @PathVariable String codigo,
            @PathVariable TipoFigurinha tipo
    ) {
        figurinhaService.remover(codigo, tipo);
    }
}
