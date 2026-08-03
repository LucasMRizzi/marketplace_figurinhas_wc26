package br.com.marketplace.controller;

import br.com.marketplace.dto.concretizacao.ConcretizacaoResponse;
import br.com.marketplace.dto.concretizacao.CriarConcretizacaoRequest;
import br.com.marketplace.service.ConcretizacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concretizacoes")
@RequiredArgsConstructor
@Tag(
        name = "Concretizações",
        description = "Gerenciamento do aceite e finalização das propostas (vendas e trocas)."
)
public class ConcretizacaoController {

    private final ConcretizacaoService concretizacaoService;

    @PostMapping("/ofertas/{idOferta}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Aceitar oferta (Concretizar)",
            description = "Gera uma concretização informando que um usuário (aceitante) fechou negócio na oferta estipulada."
    )
    public ConcretizacaoResponse criar(
            @PathVariable Integer idOferta,
            @Valid
            @RequestBody CriarConcretizacaoRequest request
    ) {
        return concretizacaoService.criar(
                idOferta,
                request
        );
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as concretizações",
            description = "Retorna o histórico completo de todas as negociações finalizadas no marketplace."
    )
    public List<ConcretizacaoResponse> listarTodas() {
        return concretizacaoService.listarTodas();
    }

    @GetMapping("/{idConcretizacao}")
    @Operation(
            summary = "Buscar concretização",
            description = "Recupera os detalhes de um fechamento de negócio pelo ID da concretização."
    )
    public ConcretizacaoResponse buscar(
            @PathVariable Integer idConcretizacao
    ) {
        return concretizacaoService.buscar(
                idConcretizacao
        );
    }

    @GetMapping("/oferta/{idOferta}")
    @Operation(
            summary = "Buscar por oferta",
            description = "Encontra a concretização (se houver) vinculada a uma oferta específica."
    )
    public ConcretizacaoResponse buscarPorOferta(
            @PathVariable Integer idOferta
    ) {
        return concretizacaoService.buscarPorOferta(
                idOferta
        );
    }

    @GetMapping("/aceitante/{cpf}")
    @Operation(
            summary = "Listar por aceitante",
            description = "Retorna todas as ofertas que foram aceitas/concretizadas por um usuário específico."
    )
    public List<ConcretizacaoResponse> listarPorAceitante(
            @PathVariable String cpf
    ) {
        return concretizacaoService
                .listarPorAceitante(cpf);
    }
}