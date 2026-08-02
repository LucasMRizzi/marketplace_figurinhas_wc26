package br.com.marketplace.controller;

import br.com.marketplace.dto.posseFigurinha.CriarPosseFigurinhaRequest;
import br.com.marketplace.dto.posseFigurinha.PosseFigurinhaResponse;
import br.com.marketplace.service.PosseFigurinhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{cpf}/posses")
@RequiredArgsConstructor
@Tag(
        name = "Inventário (Posses)",
        description = "Gerenciamento das figurinhas que os usuários possuem guardadas no inventário pessoal."
)
public class PosseFigurinhaController {

    private final PosseFigurinhaService posseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Guardar no inventário",
            description = "Cria um novo registro de posse ou apenas incrementa a quantidade caso a figurinha já esteja no inventário."
    )
    public PosseFigurinhaResponse adicionar(
            @PathVariable String cpf,
            @Valid
            @RequestBody CriarPosseFigurinhaRequest request
    ) {
        return posseService.adicionar(cpf, request);
    }

    @GetMapping
    @Operation(
            summary = "Acessar inventário",
            description = "Verifica o estoque listando todas as figurinhas que o usuário acumulou."
    )
    public List<PosseFigurinhaResponse> listar(
            @PathVariable String cpf
    ) {
        return posseService.listarPorUsuario(cpf);
    }

    @GetMapping("/{idPosse}")
    @Operation(
            summary = "Detalhar posse",
            description = "Recupera informações completas de um registro (idPosse) no inventário."
    )
    public PosseFigurinhaResponse buscar(
            @PathVariable String cpf,
            @PathVariable Integer idPosse
    ) {
        return posseService.buscar(idPosse);
    }

    @PatchMapping("/{idPosse}/quantidade/remover")
    @Operation(
            summary = "Reduzir estoque de figurinhas",
            description = "Deduz uma quantia específica da figurinha guardada. Se a quantidade esgotar, a posse é eliminada do banco."
    )
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
    @Operation(
            summary = "Deletar registro de posse",
            description = "Zera totalmente a existência desta figurinha para o usuário sem validar a quantidade restante."
    )
    public void remover(
            @PathVariable String cpf,
            @PathVariable Integer idPosse
    ) {
        posseService.remover(idPosse);
    }
}