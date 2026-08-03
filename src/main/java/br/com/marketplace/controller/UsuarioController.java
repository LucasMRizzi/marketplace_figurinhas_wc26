package br.com.marketplace.controller;

import br.com.marketplace.dto.usuario.AtualizarUsuarioRequest;
import br.com.marketplace.dto.usuario.CriarUsuarioRequest;
import br.com.marketplace.dto.usuario.UsuarioResponse;
import br.com.marketplace.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(
        name = "Usuários",
        description = "Cadastro e gerenciamento dos usuários."
)
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria um novo usuário.",
            security = {}
    )
    public UsuarioResponse criar(
            @Valid @RequestBody CriarUsuarioRequest request
    ) {
        return usuarioService.criar(request);
    }

    @GetMapping("/{cpf}")
    @Operation(summary = "Buscar usuário por CPF")
    public UsuarioResponse buscarPorCpf(
            @PathVariable String cpf
    ) {
        return usuarioService.buscarPorCpf(cpf);
    }

    @GetMapping
    @Operation(summary = "Listar usuários")
    public List<UsuarioResponse> listarTodos(){
        return usuarioService.listarTodos();
    }

    @PutMapping("/{cpf}")
    @Operation(summary = "Atualizar usuário")
    public UsuarioResponse atualizar(
            @PathVariable String cpf,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        return usuarioService.atualizar(cpf,request);
    }

    @DeleteMapping("/{cpf}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover usuário")
    public void remover(@PathVariable String cpf) {
        usuarioService.remover(cpf);
    }


}
