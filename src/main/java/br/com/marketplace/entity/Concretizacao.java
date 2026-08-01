package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "concretizacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concretizacao {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concretizacao")
    private Integer idConcretizacao;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status_do_pagamento",
            nullable = false,
            columnDefinition = "status_pagamento"
    )
    private StatusPagamento statusPagamento;

    @Column(name = "data_do_aceite", nullable = false)
    private LocalDate dataAceite;

    /**
     * =========================================================
     * Chaves Estrangeiras
     * =========================================================
     */

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_oferta",
            referencedColumnName = "id_oferta",
            nullable = false,
            unique = true
    )
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "aceitante",
            referencedColumnName = "cpf",
            nullable = false
    )
    private Usuario aceitante;

    /**
     * =========================================================
     * Relações
     * =========================================================
     */

    @OneToMany(
            mappedBy = "concretizacao",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public Concretizacao(
            Oferta oferta,
            Usuario aceitante
    ) {
        if (oferta == null) {
            throw new IllegalArgumentException(
                    "A oferta é obrigatória."
            );
        }

        if (aceitante == null) {
            throw new IllegalArgumentException(
                    "O usuário aceitante é obrigatório."
            );
        }

        if (!oferta.estaPendente()) {
            throw new IllegalStateException(
                    "Somente ofertas pendentes podem ser aceitas."
            );
        }

        if (oferta.getUsuarioProponente()
                .getCpf()
                .equals(aceitante.getCpf())) {
            throw new IllegalArgumentException(
                    "O usuário não pode aceitar a própria oferta."
            );
        }

        this.oferta = oferta;
        this.aceitante = aceitante;
        this.statusPagamento = StatusPagamento.PENDENTE;
        this.dataAceite = LocalDate.now();

        oferta.concretizar();
    }

    public void iniciarProcessamentoPagamento() {
        if (statusPagamento != StatusPagamento.PENDENTE) {
            throw new IllegalStateException(
                    "O pagamento não está pendente."
            );
        }

        this.statusPagamento =
            StatusPagamento.PROCESSAMENTO;
    }

    public void confirmarPagamento() {
        if (statusPagamento
                != StatusPagamento.PROCESSAMENTO) {
            throw new IllegalStateException(
                    "O pagamento não está em processamento."
            );
        }

        this.statusPagamento = StatusPagamento.PAGO;
    }
}