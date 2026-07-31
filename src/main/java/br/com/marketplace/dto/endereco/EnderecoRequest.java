package br.com.marketplace.dto.endereco;

public record EnderecoRequest(
        String logradouro,
        Integer numero,
        String caixaPostal,
        String cidade,
        String cep
) {
}
