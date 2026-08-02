package br.com.marketplace.dto.concretizacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarConcretizacaoRequest(

        @NotBlank(message = "O CPF do aceitante é obrigatório.")
        @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres.")
        String cpfAceitante
) {
}

/** Exemplo de arquivo json:
 * {
  "cpfAceitante": "999.888.777-66"
  }
 */