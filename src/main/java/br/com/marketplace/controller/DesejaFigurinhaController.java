package br.com.marketplace.controller;

import br.com.marketplace.dto.desejaFigurinha.CriarDesejaFigurinhaRequest;
import br.com.marketplace.dto.desejaFigurinha.DesejaFigurinhaResponse;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.service.DesejaFigurinhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{cpf}/desejos")
@RequiredArgsConstructor
@Tag(
        name = "Lista de Desejos",
        description = "Gerenciamento das figurinhas que os usuários têm interesse em adquirir."
)
public class DesejaFigurinhaController {

    private final DesejaFigurinhaService desejoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Adicionar figurinha desejada",
            description = "Insere uma figurinha específica na lista de desejos pessoal do usuário."
    )
    public DesejaFigurinhaResponse adicionar(
            @PathVariable String cpf,
            @Valid
            @RequestBody CriarDesejaFigurinhaRequest request
    ) {
        return desejoService.adicionar(cpf, request);
    }

    @GetMapping
    @Operation(
            summary = "Ver lista de desejos",
            description = "Retorna todas as figurinhas marcadas como 'desejadas' pelo usuário."
    )
    public List<DesejaFigurinhaResponse> listar(
            @PathVariable String cpf
    ) {
        return desejoService.listarPorUsuario(cpf);
    }

    @GetMapping("/{codigo}/{tipo}")
    @Operation(
            summary = "Buscar desejo específico",
            description = "Verifica se uma figurinha específica (código + tipo) está na lista de desejos do usuário."
    )
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
    @Operation(
            summary = "Remover figurinha desejada",
            description = "Remove uma figurinha da lista de desejos do usuário."
    )
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