package br.com.marketplace.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record EnderecoRequest(

        @NotBlank(message = "O logradouro é obrigatório.")
        @Size(max = 100, message = "O logradouro não deve ter mais de 100 caracteres.")
        String logradouro,

        @NotNull(message = "O número é obrigatório.")
        @Positive(message = "O número deve ser maior que zero.")
        Integer numero,

        @NotBlank(message = "A caixa postal é obrigatória.")
        @Size(max = 15, message = "A caixa postal não deve ter mais de 15 caracteres.")
        @Pattern(regexp = "\\d{1,15}", message = "A caixa postal deve conter somente números.")
        String caixaPostal,

        @NotBlank(message = "A cidade é obrigatória.")
        @Size(max = 50, message = "A cidade não deve ter mais de 50 caracteres.")
        String cidade,

        @NotBlank(message = "O cep é obrigatório.")
        @Size(max = 9, message = "O cep não deve ter mais de 9 caracteres.")
        @Pattern(regexp = "(?:\\d{8}|\\d{5}-\\d{3})", message = "O CEP deve conter 8 números.")
        String cep
) {
}


/**Contemplado em "CriarUsuarioRequest.java" */
