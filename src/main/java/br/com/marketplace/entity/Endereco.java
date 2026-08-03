package br.com.marketplace.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

}
