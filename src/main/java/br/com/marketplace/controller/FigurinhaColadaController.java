package br.com.marketplace.controller;

import br.com.marketplace.dto.figurinhaColada.CriarFigurinhaColadaRequest;
import br.com.marketplace.dto.figurinhaColada.FigurinhaColadaResponse;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.service.FigurinhaColadaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/usuarios/{cpf}/albuns/{nomeAlbum}/figurinhas"
)
@RequiredArgsConstructor
@Tag(
        name = "Figurinhas Coladas",
        description = "Gerenciamento da ação de colar figurinhas nos álbuns e cálculo de progresso."
)
public class FigurinhaColadaController {

    private final FigurinhaColadaService figurinhaColadaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Colar figurinha",
            description = "Registra que o usuário colou uma figurinha no álbum especificado."
    )
    public FigurinhaColadaResponse colar(
            @PathVariable String cpf,
            @PathVariable String nomeAlbum,
            @Valid
            @RequestBody CriarFigurinhaColadaRequest request
    ) {
        return figurinhaColadaService.colar(
                cpf,
                nomeAlbum,
                request
        );
    }

    @GetMapping
    @Operation(
            summary = "Listar figurinhas do álbum",
            description = "Retorna uma lista contendo todas as figurinhas que já foram coladas neste álbum."
    )
    public List<FigurinhaColadaResponse> listar(
            @PathVariable String cpf,
            @PathVariable String nomeAlbum
    ) {
        return figurinhaColadaService.listar(
                cpf,
                nomeAlbum
        );
    }

    @DeleteMapping("/{codigo}/{tipo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Descolar/Remover figurinha",
            description = "Remove o registro de uma figurinha colada no álbum."
    )
    public void remover(
            @PathVariable String cpf,
            @PathVariable String nomeAlbum,
            @PathVariable String codigo,
            @PathVariable TipoFigurinha tipo
    ) {
        figurinhaColadaService.remover(
                cpf,
                nomeAlbum,
                codigo,
                tipo
        );
    }
}