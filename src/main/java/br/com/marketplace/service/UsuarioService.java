package br.com.marketplace.service;

import br.com.marketplace.dto.usuario.AtualizarUsuarioRequest;
import br.com.marketplace.dto.usuario.CriarUsuarioRequest;
import br.com.marketplace.dto.usuario.UsuarioResponse;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.UsuarioMapper;
import br.com.marketplace.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponse criar(CriarUsuarioRequest request) {
        if (usuarioRepository.existsById(request.cpf())) {
            throw new RecursoJaExisteException(
                    "Já existe um usuário com esse CPF."
            );
        }

        Usuario usuario = usuarioMapper.toEntity(request);

        Usuario usuario_salvo = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario_salvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorCpf(String cpf){
        return usuarioMapper.toResponse(buscarEntidadePorCpf(cpf));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponse atualizar(
            String cpf,
            AtualizarUsuarioRequest request
    ) {
        Usuario usuario = buscarEntidadePorCpf(cpf);

        usuarioMapper.updateEntity(usuario, request);

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void remover(String cpf){
        Usuario usuario = buscarEntidadePorCpf(cpf);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarEntidadePorCpf(String cpf){
        return usuarioRepository.findById(cpf)
                .orElseThrow(()->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }
}
