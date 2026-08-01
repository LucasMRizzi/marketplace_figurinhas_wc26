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
            Oferta oferta
    ) {
        validarOferta(oferta);

        this.oferta = oferta;
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