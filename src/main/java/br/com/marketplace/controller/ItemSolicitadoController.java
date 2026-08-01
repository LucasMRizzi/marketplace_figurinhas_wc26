package br.com.marketplace.controller;

import br.com.marketplace.dto.itemSolicitado.AtualizarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.ItemSolicitadoResponse;
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
    public List<ItemSolicitadoResponse> listar(
            @PathVariable Integer idOferta
    ) {
        return itemSolicitadoService
                .listarPorTroca(idOferta);
    }

    @GetMapping("/{idItemSolicitado}")
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