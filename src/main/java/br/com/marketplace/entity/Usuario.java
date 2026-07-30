package br.com.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "cpf", length = 14)
    private String cpf;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "telefone", nullable = false, length = 15)
    private String telefone;

    @Column(
            name = "saldo",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal saldo;

    @Column(
            name = "avaliacao_media",
            nullable = false,
            precision = 3,
            scale = 2
    )
    private BigDecimal avaliacao_media;

    @Embedded
    private Endereco endereco;

    @OneToMany(mappedBy = "usuario")
    private List<PosseFigurinha> posses = new ArrayList<>();

    protected Usuario(){

    }

    public Usuario(
            String cpf,
            String nome,
            String email,
            String telefone,
            Endereco endereco
    ) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.saldo = BigDecimal.ZERO;
        this.avaliacao_media = BigDecimal.ZERO;
    }

    public void creditar(BigDecimal valor) {
        validarValorPositivo(valor);
        this.saldo = this.saldo.add(valor);
    }

    public void debitar(BigDecimal valor) {
        validarValorPositivo(valor);
        if(this.saldo.compareTo(valor) < 0){
            throw new IllegalStateException("Saldo insuficiente.");
        }
        this.saldo = this.saldo.subtract(valor);
    }

    public void atualizarAvaliacaoMedia(
            BigDecimal somaDasAvaliacoes,
            long quantidadeDeAvaliacoes
    ) {
        if(quantidadeDeAvaliacoes <= 0){
            this.avaliacao_media = BigDecimal.ZERO;
            return;
        }

        BigDecimal media = somaDasAvaliacoes.divide(
                BigDecimal.valueOf(quantidadeDeAvaliacoes),
                2,
                RoundingMode.HALF_UP
        );

        if(media.compareTo(BigDecimal.ZERO) < 0
                || media.compareTo(BigDecimal.valueOf(5)) > 0){
            throw new IllegalArgumentException(
                    "A avaliação média deve estar entre 0 e 5."
            );
        }

        this.avaliacao_media = media;
    }

    public void alterarEndereco(Endereco novo_endereco){
        if(novo_endereco == null){
            throw new IllegalArgumentException(
                    "O endereço não pode ser nulo."
            );
        }
    }

    private void validarValorPositivo(BigDecimal valor){
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(
                    "O valor deve ser maior que zero"
            );
        }
    }
}