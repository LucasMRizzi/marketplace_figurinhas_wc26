package br.com.marketplace.controller;

import br.com.marketplace.dto.itemSolicitado.AtualizarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.ItemSolicitadoResponse;
import br.com.marketplace.service.ItemSolicitadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/trocas/{idOferta}/itens-solicitados"
)
@RequiredArgsConstructor
@Tag(
        name = "Itens Solicitados",
        description = "Gerenciamento das figurinhas que são exigidas como pagamento nas propostas de troca."
)
public class ItemSolicitadoController {

    private final ItemSolicitadoService itemSolicitadoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Adicionar exigência de troca",
            description = "Insere um item desejado na lista de solicitações de uma proposta de escambo."
    )
    public ItemSolicitadoResponse criar(
            @PathVariable Integer idOferta,
            @Valid
            @RequestBody CriarItemSolicitadoRequest request
    ) {
        return itemSolicitadoService.criar(
                idOferta,
                request
        );
    }

    @GetMapping
    @Operation(
            summary = "Listar figurinhas solicitadas",
            description = "Lista todos os itens que o dono da proposta exige receber nesta negociação."
    )
    public List<ItemSolicitadoResponse> listar(
            @PathVariable Integer idOferta
    ) {
        return itemSolicitadoService
                .listarPorTroca(idOferta);
    }

    @GetMapping("/{idItemSolicitado}")
    @Operation(
            summary = "Buscar exigência",
            description = "Busca detalhes de um pedido específico dentro da proposta de troca."
    )
    public ItemSolicitadoResponse buscar(
            @PathVariable Integer idOferta,
            @PathVariable Integer idItemSolicitado
    ) {
        return itemSolicitadoService.buscar(
                idOferta,
                idItemSolicitado
        );
    }

    @PutMapping("/{idItemSolicitado}")
    @Operation(
            summary = "Atualizar exigência",
            description = "Altera as condições (como quantidade pedida) da figurinha solicitada."
    )
    public ItemSolicitadoResponse atualizar(
            @PathVariable Integer idOferta,
            @PathVariable Integer idItemSolicitado,
            @Valid
            @RequestBody AtualizarItemSolicitadoRequest request
    ) {
        return itemSolicitadoService.atualizar(
                idOferta,
                idItemSolicitado,
                request
        );
    }

    @DeleteMapping("/{idItemSolicitado}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remover exigência",
            description = "Remove a solicitação de uma figurinha específica dentro da oferta."
    )
    public void remover(
            @PathVariable Integer idOferta,
            @PathVariable Integer idItemSolicitado
    ) {
        itemSolicitadoService.remover(
                idOferta,
                idItemSolicitado
        );
    }
}