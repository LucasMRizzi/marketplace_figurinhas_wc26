package br.com.marketplace.mapper;

import br.com.marketplace.dto.usuario.AtualizarUsuarioRequest;
import br.com.marketplace.dto.usuario.CriarUsuarioRequest;
import br.com.marketplace.dto.usuario.UsuarioResponse;
import br.com.marketplace.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final EnderecoMapper enderecoMapper;

    public Usuario toEntity(
            CriarUsuarioRequest request,
            String senhaCodificada
    ) {
        return new Usuario(
                request.cpf(),
                request.nome(),
                request.email(),
                request.telefone(),
                enderecoMapper.toEntity(request.endereco()),
                senhaCodificada
        );
    }

    public UsuarioResponse toResponse(Usuario usuario){
        return new UsuarioResponse(
                usuario.getCpf(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getSaldo(),
                usuario.getAvaliacaoMedia(),
                enderecoMapper.toResponse(usuario.getEndereco())
        );
    }

    public void updateEntity(
            Usuario usuario,
            AtualizarUsuarioRequest request
    ) {
        usuario.atualizarDados(
                request.nome(),
                request.email(),
                request.telefone(),
                enderecoMapper.toEntity(request.endereco())
        );
    }

}
