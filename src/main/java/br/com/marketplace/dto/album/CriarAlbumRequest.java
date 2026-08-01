package br.com.marketplace.dto.album;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarAlbumRequest(

        @NotNull(message = "O nome não pode ser nulo.")
        @Size(max = 100, message = "O nome não pode ser maior que 100 caracteres.")
        String nome,

        @NotNull(message = "O cpf do usuário não pode ser nulo.")
        @Size(max = 14, message = "O cpf do usuário não pode ser maior que 14 caracteres.")
        String cpfUsuario
) {
}
