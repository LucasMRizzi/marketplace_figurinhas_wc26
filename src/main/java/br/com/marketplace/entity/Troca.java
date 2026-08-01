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

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Id
    @Column(name = "id_oferta")
    private Integer idOferta;

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

    /**
     * =========================================================
     * Chaves Estrangeiras
     * =========================================================
     */

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
            name = "id_oferta",
            referencedColumnName = "id_oferta"
    )
    private Oferta oferta;

    /**
     * =========================================================
     * Relações
     * =========================================================
     */

    @OneToMany(
            mappedBy = "troca",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemSolicitado> itensSolicitados = new ArrayList<>();

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public Troca(
            Oferta oferta,
            LocalDate prazoLimite,
            String descricao
    ) {
        validarOferta(oferta);
        validarPrazoLimite(prazoLimite);
        validarDescricao(descricao);

        /*TODO: if (valorDeMercado == null
                || valorDeMercado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O valor de mercado não pode ser negativo."
            );
        }*/

        this.oferta = oferta;
        //TODO: this.valorDeMercado = calcularValorDeMercado();
        this.valorDeMercado = BigDecimal.TEN;
        this.prazoLimite = prazoLimite;
        this.descricao = descricao;
    }

    public void atualizar(
            LocalDate prazoLimite,
            String descricao
    ) {
        validarPrazoLimite(prazoLimite);
        validarDescricao(descricao);

        this.prazoLimite = prazoLimite;
        this.descricao = descricao;
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

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

    private void validarPrazoLimite(LocalDate prazoLimite) {
        if (prazoLimite == null) {
            throw new IllegalArgumentException(
                    "O prazo limite é obrigatório."
            );
        }

        if (prazoLimite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "O prazo limite não pode ser anterior à data atual."
            );
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao == null) {
            throw new IllegalArgumentException(
                    "A descrição é obrigatória. Caso não exista, informe uma string vazia."
            );
        }

        if (descricao.length() > 140) {
            throw new IllegalArgumentException(
                    "A descrição não pode ter mais de 140 caracteres."
            );
        }
    }
}