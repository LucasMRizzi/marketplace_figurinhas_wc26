package br.com.marketplace.controller;

import br.com.marketplace.dto.album.AlbumResponse;
import br.com.marketplace.dto.album.CriarAlbumRequest;
import br.com.marketplace.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{cpf}/albuns")
@RequiredArgsConstructor
@Tag(
        name = "Álbuns",
        description = "Cadastro e gerenciamento dos álbuns dos usuários, incluindo o acompanhamento de completude da coleção."
)
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar álbum",
            description = "Cria um novo álbum vinculado ao usuário especificado."
    )
    public AlbumResponse criar(
            @PathVariable String cpf,
            @Valid @RequestBody CriarAlbumRequest request
    ) {
        return albumService.criar(cpf, request);
    }

    @GetMapping
    @Operation(
            summary = "Listar álbuns do usuário",
            description = "Retorna uma lista com todos os álbuns associados a um determinado usuário."
    )
    public List<AlbumResponse> listarPorUsuario(
            @PathVariable String cpf
    ) {
        return albumService.listarPorUsuario(cpf);
    }

    @GetMapping("/{nomeAlbum}")
    @Operation(
            summary = "Buscar álbum específico",
            description = "Busca os detalhes e o percentual de completude de um álbum específico pelo seu nome."
    )
    public AlbumResponse buscar(
            @PathVariable String cpf,
            @PathVariable String nomeAlbum
    ) {
        return albumService.buscar(
                cpf,
                nomeAlbum
        );
    }

    @DeleteMapping("/{nomeAlbum}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remover álbum",
            description = "Exclui um álbum específico do usuário."
    )
    public void remover(
            @PathVariable String cpf,
            @PathVariable String nomeAlbum
    ) {
        albumService.remover(cpf, nomeAlbum);
    }
}