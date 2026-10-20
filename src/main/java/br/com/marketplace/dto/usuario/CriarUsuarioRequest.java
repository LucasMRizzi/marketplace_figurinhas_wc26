package br.com.marketplace.dto.usuario;

import br.com.marketplace.dto.endereco.EnderecoRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record CriarUsuarioRequest(

        @NotBlank(message = "O CPF é obrigatório.")
        @Size(max = 14, message = "O cpf não pode ser maior que 14 caracteres.")
        @Pattern(regexp = "(?:\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})", message = "O CPF deve conter 11 números.")
        @CPF(message = "O CPF informado é inválido.")
        String cpf,

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100, message = "O nome não pode ser maior que 100 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        @Size(max = 100, message = "O email não pode ser maior que 100 caracteres.")
        String email,

        @NotBlank(message = "O telefone é obrigatório.")
        @Size(max = 15, message = "O telefone não pode ser maior que 15 caracteres.")
        @Pattern(regexp = "(?:\\d{10,11}|\\(\\d{2}\\) ?\\d{4,5}-\\d{4})", message = "O telefone deve conter DDD e 10 ou 11 números.")
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
