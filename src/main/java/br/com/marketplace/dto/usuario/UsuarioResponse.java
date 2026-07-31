package br.com.marketplace.dto.usuario;


import br.com.marketplace.dto.endereco.EnderecoResponse;

import java.math.BigDecimal;

public record UsuarioResponse(
        String cpf,
        String nome,
        String email,
        String telefone,
        BigDecimal saldo,
        BigDecimal avaliacaoMedia,
        EnderecoResponse endereco
) {
}
