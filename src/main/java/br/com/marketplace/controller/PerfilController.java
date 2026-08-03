package br.com.marketplace.controller;

import br.com.marketplace.dto.usuario.AtualizarUsuarioRequest;
import br.com.marketplace.dto.usuario.UsuarioResponse;
import br.com.marketplace.mapper.UsuarioMapper;
import br.com.marketplace.security.UsuarioDetails;
import br.com.marketplace.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;

    @GetMapping
    public UsuarioResponse buscarPerfil(
            @AuthenticationPrincipal
            UsuarioDetails usuarioDetails
    ) {
        return usuarioMapper.toResponse(
                usuarioDetails.getUsuario()
        );
    }

    @PutMapping
    public UsuarioResponse atualizarPerfil(
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        return usuarioService.atualizar(
                usuarioDetails.getUsuario().getCpf(),
                request
        );
    }
}
