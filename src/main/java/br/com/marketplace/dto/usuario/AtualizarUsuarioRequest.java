package br.com.marketplace.dto.usuario;

import br.com.marketplace.dto.endereco.EnderecoRequest;

public record AtualizarUsuarioRequest(
        String nome,
        String email,
        String telefone,
        EnderecoRequest endereco
) {
}
