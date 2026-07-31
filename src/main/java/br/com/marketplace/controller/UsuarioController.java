package br.com.marketplace.controller;

import br.com.marketplace.dto.usuario.AtualizarUsuarioRequest;
import br.com.marketplace.dto.usuario.CriarUsuarioRequest;
import br.com.marketplace.dto.usuario.UsuarioResponse;
import br.com.marketplace.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(
            @Valid @RequestBody CriarUsuarioRequest request
    ) {
        return usuarioService.criar(request);
    }

    @GetMapping("/{cpf}")
    public UsuarioResponse buscarPorCpf(
            @PathVariable String cpf
    ) {
        return usuarioService.buscarPorCpf(cpf);
    }

    @GetMapping
    public List<UsuarioResponse> listarTodos(){
        return usuarioService.listarTodos();
    }

    @PutMapping("/{cpf}")
    public UsuarioResponse atualizar(
            @PathVariable String cpf,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        return usuarioService.atualizar(cpf,request);
    }

    @DeleteMapping("/{cpf}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String cpf) {
        usuarioService.remover(cpf);
    }

}
