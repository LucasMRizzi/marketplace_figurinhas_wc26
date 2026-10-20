package br.com.marketplace.controller;

import br.com.marketplace.dto.oferta.OfertaResponse;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.service.OfertaService;
import br.com.marketplace.security.UsuarioDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.math.BigDecimal;

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

    @GetMapping("/busca")
    public List<OfertaResponse> buscarAvancado(
            @RequestParam(required = false) List<TipoOferta> tipos,
            @RequestParam(required = false) List<TipoFigurinha> tiposFigurinha,
            @RequestParam(required = false) BigDecimal valorMinimo,
            @RequestParam(required = false) BigDecimal valorMaximo,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String termo,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ofertaService.buscarAvancado(
                tipos, tiposFigurinha, valorMinimo, valorMaximo, cidade, termo,
                usuarioDetails.getUsuario().getCpf()
        );
    }

    @GetMapping("/detalhes/{idOferta}")
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
