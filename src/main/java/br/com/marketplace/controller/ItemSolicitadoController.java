package br.com.marketplace.controller;

import br.com.marketplace.dto.itemsolicitado.AtualizarItemSolicitadoRequest;
import br.com.marketplace.dto.itemsolicitado.CriarItemSolicitadoRequest;
import br.com.marketplace.dto.itemsolicitado.ItemSolicitadoResponse;
import br.com.marketplace.service.ItemSolicitadoService;
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
public class ItemSolicitadoController {

    private final ItemSolicitadoService itemSolicitadoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemSolicitadoResponse criar(
            @PathVariable Long idOferta,
            @Valid
            @RequestBody CriarItemSolicitadoRequest request
    ) {
        return itemSolicitadoService.criar(
                idOferta,
                request
        );
    }

    @GetMapping
    public List<ItemSolicitadoResponse> listar(
            @PathVariable Long idOferta
    ) {
        return itemSolicitadoService
                .listarPorTroca(idOferta);
    }

    @GetMapping("/{idItemSolicitado}")
    public ItemSolicitadoResponse buscar(
            @PathVariable Long idOferta,
            @PathVariable Long idItemSolicitado
    ) {
        return itemSolicitadoService.buscar(
                idOferta,
                idItemSolicitado
        );
    }

    @PutMapping("/{idItemSolicitado}")
    public ItemSolicitadoResponse atualizar(
            @PathVariable Long idOferta,
            @PathVariable Long idItemSolicitado,
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
    public void remover(
            @PathVariable Long idOferta,
            @PathVariable Long idItemSolicitado
    ) {
        itemSolicitadoService.remover(
                idOferta,
                idItemSolicitado
        );
    }
}