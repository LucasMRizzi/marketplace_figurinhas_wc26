package br.com.marketplace.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endereco {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Column(name = "logradouro", nullable = false, length = 100)
    private String logradouro;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "caixa_postal", nullable = false, length = 15)
    private String caixaPostal;

    @Column(name = "cidade", nullable = false, length = 50)
    private String cidade;

    @Column(name = "cep", nullable = false, length = 13)
    private String cep;

    public Endereco(String logradouro, Integer numero, String caixaPostal, String cidade, String cep) {
        if (logradouro == null || logradouro.isBlank() || logradouro.length() > 100) {
            throw new IllegalArgumentException("O logradouro é obrigatório e deve possuir até 100 caracteres.");
        }
        if (numero == null || numero <= 0) {
            throw new IllegalArgumentException("O número do endereço deve ser maior que zero.");
        }
        if (caixaPostal == null || !caixaPostal.matches("\\d{1,15}")) {
            throw new IllegalArgumentException("A caixa postal deve ser armazenada somente com números.");
        }
        if (cidade == null || cidade.isBlank() || cidade.length() > 50) {
            throw new IllegalArgumentException("A cidade é obrigatória e deve possuir até 50 caracteres.");
        }
        if (cep == null || !cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("O CEP deve ser armazenado com 8 números.");
        }
        this.logradouro = logradouro;
        this.numero = numero;
        this.caixaPostal = caixaPostal;
        this.cidade = cidade;
        this.cep = cep;
    }

}
