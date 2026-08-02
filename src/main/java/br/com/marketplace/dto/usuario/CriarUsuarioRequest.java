package br.com.marketplace.dto.usuario;

import br.com.marketplace.dto.endereco.EnderecoRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CriarUsuarioRequest(

        @NotNull(message = "O cpf não pode ser nulo.")
        @Size(max = 14, message = "O cpf não pode ser maior que 14 caracteres.")
        String cpf,

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
        EnderecoRequest endereco,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(
                min = 8,
                max = 16,
                message = "A senha deve possuir entre 8 e 16 caracteres."
        )
        String senha
) {
}

/** Exemplo de arquivo json:
 * {
  "cpf": "123.456.789-00",
  "nome": "João da Silva",
  "email": "joao.silva@email.com",
  "telefone": "(19) 99999-9999",
  "senha": "senhaSegura123",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": 123,
    "caixaPostal": "Cx 45",
    "cidade": "Rio Claro",
    "cep": "13500-000"
  }
  }
 */