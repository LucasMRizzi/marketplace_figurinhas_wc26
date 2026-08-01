package br.com.marketplace.controller;

import br.com.marketplace.dto.itemOfertado.AtualizarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.ItemOfertadoResponse;
import br.com.marketplace.service.ItemOfertadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/ofertas/{idOferta}/itens-ofertados"
)
@RequiredArgsConstructor
public class ItemOfertadoController {

    private final ItemOfertadoService itemOfertadoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemOfertadoResponse criar(
            @PathVariable Integer idOferta,
            @Valid
            @RequestBody CriarItemOfertadoRequest request
    ) {
        return itemOfertadoService.criar(
                idOferta,
                request
        );
    }

    @GetMapping
    public List<ItemOfertadoResponse> listar(
            @PathVariable Integer idOferta
    ) {
        return itemOfertadoService
                .listarPorOferta(idOferta);
    }

    @GetMapping("/{idItem}")
    public ItemOfertadoResponse buscar(
            @PathVariable Integer idOferta,
            @PathVariable Integer idItem
    ) {
        return itemOfertadoService.buscar(
                idOferta,
                idItem
        );
    }

    @PutMapping("/{idItem}")
    public ItemOfertadoResponse atualizar(
            @PathVariable Integer idOferta,
            @PathVariable Integer idItem,
            @Valid
            @RequestBody AtualizarItemOfertadoRequest request
    ) {
        return itemOfertadoService.atualizar(
                idOferta,
                idItem,
                request
        );
    }

    @DeleteMapping("/{idItem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable Integer idOferta,
            @PathVariable Integer idItem
    ) {
        itemOfertadoService.remover(
                idOferta,
                idItem
        );
    }
}