package br.com.marketplace.controller;

import br.com.marketplace.dto.figurinhaColada.CriarFigurinhaColadaRequest;
import br.com.marketplace.dto.figurinhaColada.FigurinhaColadaResponse;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.service.FigurinhaColadaService;
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
public class FigurinhaColadaController {

    private final FigurinhaColadaService figurinhaColadaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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