package br.com.marketplace.controller;

import br.com.marketplace.dto.avaliacao.AtualizarAvaliacaoRequest;
import br.com.marketplace.dto.avaliacao.AvaliacaoResponse;
import br.com.marketplace.dto.avaliacao.CriarAvaliacaoRequest;
import br.com.marketplace.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
@Tag(
        name = "Avaliações",
        description = "Gerenciamento das avaliações feitas entre usuários após as negociações."
)
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping("/concretizacoes/{idConcretizacao}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar avaliação",
            description = "Registra uma nova avaliação (nota e comentário) referente a uma negociação concretizada."
    )
    public AvaliacaoResponse criar(
            @PathVariable Integer idConcretizacao,
            @Valid
            @RequestBody CriarAvaliacaoRequest request
    ) {
        return avaliacaoService.criar(
                idConcretizacao,
                request
        );
    }

    @GetMapping(
            "/{idConcretizacao}/{cpfAvaliador}/{cpfAvaliado}"
    )
    @Operation(
            summary = "Buscar avaliação",
            description = "Busca uma avaliação específica com base na concretização, e nos CPFs do avaliador e avaliado."
    )
    public AvaliacaoResponse buscar(
            @PathVariable Integer idConcretizacao,
            @PathVariable String cpfAvaliador,
            @PathVariable String cpfAvaliado
    ) {
        return avaliacaoService.buscar(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao
        );
    }

    @GetMapping("/avaliados/{cpf}")
    @Operation(
            summary = "Listar avaliações recebidas",
            description = "Retorna todo o histórico de avaliações que um usuário específico recebeu."
    )
    public List<AvaliacaoResponse> listarPorAvaliado(
            @PathVariable String cpf
    ) {
        return avaliacaoService
                .listarPorAvaliado(cpf);
    }

    @GetMapping("/avaliadores/{cpf}")
    @Operation(
            summary = "Listar avaliações feitas",
            description = "Retorna todo o histórico de avaliações que um usuário específico realizou."
    )
    public List<AvaliacaoResponse> listarPorAvaliador(
            @PathVariable String cpf
    ) {
        return avaliacaoService
                .listarPorAvaliador(cpf);
    }

    @PutMapping(
            "/{idConcretizacao}/{cpfAvaliador}/{cpfAvaliado}"
    )
    @Operation(
            summary = "Atualizar avaliação",
            description = "Modifica a nota e/ou o comentário de uma avaliação já existente."
    )
    public AvaliacaoResponse atualizar(
            @PathVariable Integer idConcretizacao,
            @PathVariable String cpfAvaliador,
            @PathVariable String cpfAvaliado,
            @Valid
            @RequestBody AtualizarAvaliacaoRequest request
    ) {
        return avaliacaoService.atualizar(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao,
                request
        );
    }

    @DeleteMapping(
            "/{idConcretizacao}/{cpfAvaliador}/{cpfAvaliado}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remover avaliação",
            description = "Exclui uma avaliação feita em uma concretização."
    )
    public void remover(
            @PathVariable Integer idConcretizacao,
            @PathVariable String cpfAvaliador,
            @PathVariable String cpfAvaliado
    ) {
        avaliacaoService.remover(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao
        );
    }
}