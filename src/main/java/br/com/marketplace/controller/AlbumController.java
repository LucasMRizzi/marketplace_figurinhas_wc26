package br.com.marketplace.controller;

import br.com.marketplace.dto.album.AlbumResponse;
import br.com.marketplace.dto.album.CriarAlbumRequest;
import br.com.marketplace.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{cpf}/albuns")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponse criar(
            @PathVariable String cpf,
            @Valid @RequestBody CriarAlbumRequest request
    ) {
        return albumService.criar(cpf, request);
    }

    @GetMapping
    public List<AlbumResponse> listarPorUsuario(
            @PathVariable String cpf
    ) {
        return albumService.listarPorUsuario(cpf);
    }

    @GetMapping("/{nomeAlbum}")
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
    public void remover(
            @PathVariable String cpf,
            @PathVariable String nomeAlbum
    ) {
        albumService.remover(cpf, nomeAlbum);
    }
}
