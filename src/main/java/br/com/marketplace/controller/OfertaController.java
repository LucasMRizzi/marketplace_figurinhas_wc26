package br.com.marketplace.controller;

import br.com.marketplace.dto.oferta.OfertaResponse;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.service.OfertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
public class OfertaController {

    private final OfertaService ofertaService;

    @GetMapping
    public List<OfertaResponse> listar(
            @RequestParam(required = false)
            TipoOferta tipo,

            @RequestParam(required = false)
            StatusOferta status
    ) {
        return ofertaService.listar(tipo, status);
    }

    @GetMapping("/{idOferta}")
    public OfertaResponse buscar(
            @PathVariable Integer idOferta
    ) {
        return ofertaService.buscar(idOferta);
    }

    @GetMapping("/usuario/{cpf}")
    public List<OfertaResponse> listarPorUsuario(
            @PathVariable String cpf
    ) {
        return ofertaService.listarPorUsuario(cpf);
    }

    @PatchMapping("/{idOferta}/expirar")
    public OfertaResponse expirar(
            @PathVariable Integer idOferta
    ) {
        return ofertaService.expirar(idOferta);
    }
}