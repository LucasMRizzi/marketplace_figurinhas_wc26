package br.com.marketplace.dto.endereco;

public record EnderecoResponse(
        String logradouro,
        Integer numero,
        String caixaPostal,
        String cidade,
        String cep
) {
}