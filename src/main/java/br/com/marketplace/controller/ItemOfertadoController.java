package br.com.marketplace.controller;

import br.com.marketplace.dto.itemOfertado.AtualizarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.ItemOfertadoResponse;
import br.com.marketplace.service.ItemOfertadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Itens Ofertados",
        description = "Gerenciamento das figurinhas do inventário que estão sendo oferecidas nas negociações."
)
public class ItemOfertadoController {

    private final ItemOfertadoService itemOfertadoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Adicionar item à oferta",
            description = "Vincula uma figurinha que o proponente possui como item de troca/venda na oferta."
    )
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
    @Operation(
            summary = "Listar itens ofertados",
            description = "Retorna todos os itens vinculados que estão sendo entregues nesta oferta."
    )
    public List<ItemOfertadoResponse> listar(
            @PathVariable Integer idOferta
    ) {
        return itemOfertadoService
                .listarPorOferta(idOferta);
    }

    @GetMapping("/{idItem}")
    @Operation(
            summary = "Buscar item ofertado",
            description = "Visualiza os detalhes, condições e fotos de um item ofertado específico."
    )
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
    @Operation(
            summary = "Atualizar item ofertado",
            description = "Permite alterar quantidade, condição ou imagem do item ofertado, caso a oferta esteja pendente."
    )
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
    @Operation(
            summary = "Remover item ofertado",
            description = "Desvincula uma figurinha da oferta em andamento."
    )
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