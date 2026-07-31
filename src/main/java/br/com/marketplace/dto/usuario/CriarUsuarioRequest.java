package br.com.marketplace.dto.usuario;

import br.com.marketplace.dto.endereco.EnderecoRequest;

import java.math.BigDecimal;

public record CriarUsuarioRequest(
        String cpf,
        String nome,
        String email,
        String telefone,
        EnderecoRequest endereco
) {
}
