package br.com.marketplace.controller;

import br.com.marketplace.dto.itemofertado.AtualizarItemOfertadoRequest;
import br.com.marketplace.dto.itemofertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemofertado.ItemOfertadoResponse;
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
            @PathVariable Long idOferta,
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
            @PathVariable Long idOferta
    ) {
        return itemOfertadoService
                .listarPorOferta(idOferta);
    }

    @GetMapping("/{idItem}")
    public ItemOfertadoResponse buscar(
            @PathVariable Long idOferta,
            @PathVariable Long idItem
    ) {
        return itemOfertadoService.buscar(
                idOferta,
                idItem
        );
    }

    @PutMapping("/{idItem}")
    public ItemOfertadoResponse atualizar(
            @PathVariable Long idOferta,
            @PathVariable Long idItem,
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
            @PathVariable Long idOferta,
            @PathVariable Long idItem
    ) {
        itemOfertadoService.remover(
                idOferta,
                idItem
        );
    }
}