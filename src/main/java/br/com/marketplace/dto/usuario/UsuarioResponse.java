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

/** Exemplo de arquivo json:
 * {
  "cpf": "123.456.789-00",
  "nome": "João da Silva Sauro",
  "email": "joao.novo@email.com",
  "telefone": "(19) 98888-8888",
  "saldo": 350.75,
  "avaliacaoMedia": 4.8,
  "endereco": {
    "logradouro": "Avenida Brasil",
    "numero": 1000,
    "caixaPostal": "N/A",
    "cidade": "Rio Claro",
    "cep": "13506-000"
  }
  }
 */
