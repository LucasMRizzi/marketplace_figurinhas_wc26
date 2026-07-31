package br.com.marketplace.controller;

import br.com.marketplace.dto.venda.AtualizarVendaRequest;
import br.com.marketplace.dto.venda.CriarVendaRequest;
import br.com.marketplace.dto.venda.VendaResponse;
import br.com.marketplace.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping("/api/usuarios/{cpf}/vendas")
    @ResponseStatus(HttpStatus.CREATED)
    public VendaResponse criar(
            @PathVariable String cpf,
            @Valid @RequestBody CriarVendaRequest request
    ) {
        return vendaService.criar(cpf, request);
    }

    @GetMapping("/api/vendas")
    public List<VendaResponse> listarTodas() {
        return vendaService.listarTodas();
    }

    @GetMapping("/api/vendas/{idOferta}")
    public VendaResponse buscar(
            @PathVariable Integer idOferta
    ) {
        return vendaService.buscar(idOferta);
    }

    @GetMapping("/api/usuarios/{cpf}/vendas")
    public List<VendaResponse> listarPorProponente(
            @PathVariable String cpf
    ) {
        return vendaService.listarPorProponente(cpf);
    }

    @PatchMapping("/api/vendas/{idOferta}/preco")
    public VendaResponse atualizarPreco(
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
    public void remover(
            @PathVariable Integer idOferta
    ) {
        vendaService.remover(idOferta);
    }
}