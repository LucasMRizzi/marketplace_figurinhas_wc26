package br.com.marketplace.controller;

import br.com.marketplace.dto.oferta.OfertaResponse;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.service.OfertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
@Tag(
        name = "Ofertas",
        description = "Consultas e ações gerais aplicáveis a todas as ofertas (matriz de Vendas e Trocas)."
)
public class OfertaController {

    private final OfertaService ofertaService;

    @GetMapping
    @Operation(
            summary = "Pesquisar ofertas gerais",
            description = "Retorna uma listagem global das propostas, permitindo aplicar filtros para tipo (Venda/Troca) e status (Pendente/Concretizada/Expirada)."
    )
    public List<OfertaResponse> listar(
            @RequestParam(required = false)
            TipoOferta tipo,

            @RequestParam(required = false)
            StatusOferta status
    ) {
        return ofertaService.listar(tipo, status);
    }

    @GetMapping("/{idOferta}")
    @Operation(
            summary = "Ver dados da oferta",
            description = "Obtém as informações matrizes (criador, data limite, status) da proposta especificada."
    )
    public OfertaResponse buscar(
            @PathVariable Integer idOferta
    ) {
        return ofertaService.buscar(idOferta);
    }

    @GetMapping("/usuario/{cpf}")
    @Operation(
            summary = "Ver histórico de propostas",
            description = "Localiza e retorna as ofertas publicadas por um usuário específico."
    )
    public List<OfertaResponse> listarPorUsuario(
            @PathVariable String cpf
    ) {
        return ofertaService.listarPorUsuario(cpf);
    }

    @PatchMapping("/{idOferta}/expirar")
    @Operation(
            summary = "Expirar proposta manual",
            description = "Provoca manualmente a expiração de uma oferta ativa, travando a negociação."
    )
    public OfertaResponse expirar(
            @PathVariable Integer idOferta
    ) {
        return ofertaService.expirar(idOferta);
    }
}