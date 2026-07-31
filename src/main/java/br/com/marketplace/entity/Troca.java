package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.TipoOferta;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "troca")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Troca {

    @Id
    @Column(name = "id_oferta")
    private Integer idOferta;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "id_oferta",
            referencedColumnName = "id_oferta"
    )
    private Oferta oferta;

    @Column(
            name = "valor_de_mercado",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal valorDeMercado;

    @Column(name = "prazo_limite", nullable = false)
    private LocalDate prazoLimite;

    @Column(
            name = "descricao",
            nullable = false,
            length = 140
    )
    private String descricao;

    @OneToMany(
            mappedBy = "troca",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemSolicitado> itensSolicitados =
            new ArrayList<>();

    public Troca(
            Oferta oferta,
            LocalDate prazoLimite,
            String descricao
    ) {
        validarOferta(oferta);

        /*if (valorDeMercado == null
                || valorDeMercado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O valor de mercado não pode ser negativo."
            );
        }*/

        if (prazoLimite == null) {
            throw new IllegalArgumentException(
                    "O prazo limite é obrigatório."
            );
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição é obrigatória."
            );
        }

        if (descricao.length() > 140) {
            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 140 caracteres."
            );
        }

        this.oferta = oferta;
        //TODO: this.valorDeMercado = calcularValorDeMercado();
        this.valorDeMercado = BigDecimal.TEN;
        this.prazoLimite = prazoLimite;
        this.descricao = descricao;
    }

    private void validarOferta(Oferta oferta) {
        if (oferta == null) {
            throw new IllegalArgumentException(
                    "A oferta é obrigatória."
            );
        }

        if (oferta.getTipo() != TipoOferta.TROCA) {
            throw new IllegalArgumentException(
                    "A oferta deve ser do tipo troca."
            );
        }
    }

    public void atualizar(
            LocalDate prazoLimite,
            String descricao
    ) {
        if (prazoLimite == null) {
            throw new IllegalArgumentException(
                    "A data é obrigatória."
            );
        }

        if (prazoLimite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "A data não pode ser anterior à data atual."
            );
        }

        if (descricao == null) {
            throw new IllegalArgumentException(
                    "A descricão é obrigatória ou deve estar em branco."
            );
        }

        if (descricao.length() > 140) {
            throw new IllegalArgumentException(
                    "A descrição não pode ser maior que 140 caracteres."
            );
        }

        this.prazoLimite = prazoLimite;
        this.descricao = descricao;
    }
}