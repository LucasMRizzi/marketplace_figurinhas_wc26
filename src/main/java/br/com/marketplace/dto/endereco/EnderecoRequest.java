package br.com.marketplace.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(

        @NotBlank(message = "O logradouro é obrigatório.")
        @Size(max = 100, message = "O logradouro não deve ter mais de 100 caracteres.")
        String logradouro,

        @NotBlank(message = "O número é obrigatório.")
        @Positive(message = "O número deve ser maior que zero.")
        Integer numero,

        @NotBlank(message = "A caixa postal é obrigatória.")
        @Size(max = 15, message = "A caixa postal não deve ter mais de 15 caracteres.")
        String caixaPostal,

        @NotBlank(message = "A cidade é obrigatória.")
        @Size(max = 50, message = "A cidade não deve ter mais de 50 caracteres.")
        String cidade,

        @NotBlank(message = "O cep é obrigatório.")
        @Size(max = 13, message = "O cep não deve ter mais de 13 caracteres.")
        String cep
) {
}
