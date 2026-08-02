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
