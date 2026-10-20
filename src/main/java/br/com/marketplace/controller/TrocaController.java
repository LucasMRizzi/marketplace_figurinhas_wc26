package br.com.marketplace.controller;

import br.com.marketplace.dto.troca.CriarTrocaRequest;
import br.com.marketplace.dto.troca.TrocaResponse;
import br.com.marketplace.service.TrocaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Trocas",
        description = "Gerenciamento de propostas de escambo (troca de figurinhas por figurinhas)."
)
public class TrocaController {

    private final TrocaService trocaService;

    @PostMapping("/api/usuarios/{cpf}/trocas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar proposta de escambo",
            description = "Cria uma proposta principal de Troca vinculando a nova oferta ao proponente."
    )
    public TrocaResponse criar(
            @PathVariable String cpf,
            @Valid @RequestBody CriarTrocaRequest request
    ) {
        return trocaService.criar(cpf, request);
    }

    @GetMapping("/api/trocas")
    @Operation(
            summary = "Consultar painel de trocas",
            description = "Varre o banco listando todas as negociações categorizadas sob o formato de Troca."
    )
    public List<TrocaResponse> listarTodas() {
        return trocaService.listarTodas();
    }

    @GetMapping("/api/trocas/{idOferta}")
    @Operation(
            summary = "Ver detalhes da troca",
            description = "Examina as informações de uma proposta específica, incluindo sua matriz (Oferta)."
    )
    public TrocaResponse buscar(
            @PathVariable Integer idOferta
    ) {
        return trocaService.buscar(idOferta);
    }

    @GetMapping("/api/usuarios/{cpf}/trocas")
    @Operation(
            summary = "Inventariar trocas do usuário",
            description = "Extrai todas as propostas ativas/inativas que tenham o CPF referenciado como proponente."
    )
    public List<TrocaResponse> listarPorProponente(
            @PathVariable String cpf
    ) {
        return trocaService.listarPorProponente(cpf);
    }

    @DeleteMapping("/api/trocas/{idOferta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar escambo",
            description = "Remove efetivamente o registro de Troca e anula a oferta matriz atrelada."
    )
    public void remover(
            @PathVariable Integer idOferta
    ) {
        trocaService.remover(idOferta);
    }
}