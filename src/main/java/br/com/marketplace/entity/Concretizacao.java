package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
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
    private LocalDateTime dataAceite;

    @Column(name = "codigo_transacao", length = 100)
    private String codigoTransacao;

    @Column(name = "motivo_recusa", length = 255)
    private String motivoRecusa;

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
        this.dataAceite = LocalDateTime.now();

        oferta.concretizar();
    }

    public void iniciarPagamento() {
        if (statusPagamento != StatusPagamento.PENDENTE) {
            throw new IllegalStateException(
                    "Apenas pagamentos pendentes podem ser processados."
            );
        }

        this.statusPagamento =
                StatusPagamento.PROCESSAMENTO;
    }

    public void confirmarPagamento(String codigoTransacao) {
        if (statusPagamento != StatusPagamento.PROCESSAMENTO) {
            throw new IllegalStateException(
                    "O pagamento deve estar em processamento."
            );
        }

        if (codigoTransacao == null
                || codigoTransacao.isBlank()) {
            throw new IllegalArgumentException(
                    "O código da transação é obrigatório."
            );
        }

        this.statusPagamento = StatusPagamento.PAGO;
        this.codigoTransacao = codigoTransacao;
        this.motivoRecusa = null;
    }

    public void recusarPagamento(String motivo) {
        if (statusPagamento != StatusPagamento.PROCESSAMENTO) {
            throw new IllegalStateException(
                    "O pagamento deve estar em processamento."
            );
        }

        this.statusPagamento = StatusPagamento.RECUSADO;
        this.codigoTransacao = null;
        this.motivoRecusa = motivo;
    }

    public Usuario getUsuarioProponente(){
        return oferta.getUsuarioProponente();
    }
}