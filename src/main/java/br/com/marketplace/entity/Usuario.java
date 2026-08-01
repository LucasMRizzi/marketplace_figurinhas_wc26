package br.com.marketplace.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "usuario")
public class Usuario {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

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
    private BigDecimal avaliacaoMedia;

    @Embedded
    private Endereco endereco;

    /**
     * =========================================================
     * Relações
     * =========================================================
     */

    @OneToMany(mappedBy = "usuario")
    private List<PosseFigurinha> posses = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Album> albuns = new ArrayList<>();

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public Usuario(
            String cpf,
            String nome,
            String email,
            String telefone,
            Endereco endereco
    ) {
        validarNome(nome);
        validarCpf(cpf);
        validarEmail(email);
        validarTelefone(telefone);
        validarEndereco(endereco);

        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.saldo = BigDecimal.ZERO;
        this.avaliacaoMedia = BigDecimal.ZERO;
    }

    public void atualizarDados(
            String nome,
            String email,
            String telefone,
            Endereco endereco
    ) {
        validarNome(nome);
        validarEmail(email);
        validarTelefone(telefone);
        validarEndereco(endereco);


        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
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
            this.avaliacaoMedia = BigDecimal.ZERO;
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

        this.avaliacaoMedia = media;
    }

    public void alterarEndereco(Endereco novo_endereco){
        if(novo_endereco == null){
            throw new IllegalArgumentException(
                    "O endereço não pode ser nulo."
            );
        }
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

    private void validarValorPositivo(BigDecimal valor){
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(
                    "O valor deve ser maior que zero"
            );
        }
    }

    private void validarNome(String nome){
        if(nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome é obrigatório."
            );
        }

        if(nome.length() > 100) {
            throw new IllegalArgumentException(
                    "O nome não pode ter mais de 100 caracteres."
            );
        }
    }

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException(
                    "O CPF é obrigatório."
            );
        }

        if (cpf.length() > 14) {
            throw new IllegalArgumentException(
                    "O CPF não pode ter mais de 14 caracteres."
            );
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "O e-mail é obrigatório."
            );
        }

        if (email.length() > 100) {
            throw new IllegalArgumentException(
                    "O e-mail não pode ter mais de 100 caracteres."
            );
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(
                    "O e-mail informado é inválido."
            );
        }
    }

    private void validarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException(
                    "O telefone é obrigatório."
            );
        }

        if (telefone.length() > 15) {
            throw new IllegalArgumentException(
                    "O telefone não pode ter mais de 15 caracteres."
            );
        }
    }

    private void validarEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException(
                    "O endereço é obrigatório."
            );
        }
    }
}