package br.com.marketplace.controller;

import br.com.marketplace.dto.venda.AtualizarVendaRequest;
import br.com.marketplace.dto.venda.CriarVendaRequest;
import br.com.marketplace.dto.venda.VendaResponse;
import br.com.marketplace.service.VendaService;
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
        name = "Vendas",
        description = "Gerenciamento de propostas de venda de figurinhas por dinheiro."
)
public class VendaController {

    private final VendaService vendaService;

    @PostMapping("/api/usuarios/{cpf}/vendas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Abrir anúncio de venda",
            description = "Instancia uma nova Oferta vinculando aos dados financeiros diretos (Venda)."
    )
    public VendaResponse criar(
            @PathVariable String cpf,
            @Valid @RequestBody CriarVendaRequest request
    ) {
        return vendaService.criar(cpf, request);
    }

    @GetMapping("/api/vendas")
    @Operation(
            summary = "Navegar vitrine de vendas",
            description = "Visualiza toda a lista financeira atual e finalizada das propostas de vendas no sistema."
    )
    public List<VendaResponse> listarTodas() {
        return vendaService.listarTodas();
    }

    @GetMapping("/api/vendas/{idOferta}")
    @Operation(
            summary = "Detalhar anúncio",
            description = "Expande a leitura das regras financeiras e a condição de uma proposta por ID."
    )
    public VendaResponse buscar(
            @PathVariable Integer idOferta
    ) {
        return vendaService.buscar(idOferta);
    }

    @GetMapping("/api/usuarios/{cpf}/vendas")
    @Operation(
            summary = "Buscar anúncios do usuário",
            description = "Carrega e estrutura as propostas em que o usuário listado é o originador das Vendas."
    )
    public List<VendaResponse> listarPorProponente(
            @PathVariable String cpf
    ) {
        return vendaService.listarPorProponente(cpf);
    }

    @PatchMapping("/api/vendas/{idOferta}")
    @Operation(
            summary = "Atualizar valores",
            description = "Altera as métricas ou valores atrelados a uma negociação de venda já instanciada."
    )
    public VendaResponse atualizar(
            @PathVariable Integer idOferta,
            @Valid
            @RequestBody AtualizarVendaRequest request
    ) {
        return vendaService.atualizar(
                idOferta,
                request
        );
    }

    @DeleteMapping("/api/vendas/{idOferta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Anular anúncio de venda",
            description = "Destrói a negociação monetária e remove o registro original (Oferta) do banco."
    )
    public void remover(
            @PathVariable Integer idOferta
    ) {
        vendaService.remover(idOferta);
    }
}