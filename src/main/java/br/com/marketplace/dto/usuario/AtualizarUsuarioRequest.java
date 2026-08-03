package br.com.marketplace.dto.usuario;

import br.com.marketplace.dto.endereco.EnderecoRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(

        @NotNull(message = "O nome não pode ser nulo.")
        @Size(max = 100, message = "O nome não pode ser maior que 100 caracteres.")
        String nome,

        @NotNull(message = "O email não pode ser nulo.")
        @Size(max = 100, message = "O email não pode ser maior que 100 caracteres.")
        String email,

        @NotNull(message = "O telefone não pode ser nulo.")
        @Size(max = 15, message = "O telefone não pode ser maior que 15 caracteres.")
        String telefone,

        @NotNull(message = "O endereco não pode ser nulo.")
        EnderecoRequest endereco
) {
}

/** Exemplo de arquivo json:
 * {
  "nome": "João da Silva Sauro",
  "email": "joao.novo@email.com",
  "telefone": "(19) 98888-8888",
  "endereco": {
    "logradouro": "Avenida Brasil",
    "numero": 1000,
    "caixaPostal": "N/A",
    "cidade": "Rio Claro",
    "cep": "13506-000"
  }
  }
 */
