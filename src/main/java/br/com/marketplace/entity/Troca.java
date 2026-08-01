package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RegraDeNegocioException;
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
        oferta.associarTroca(this);
    }

    public void adicionarItemSolicitado(ItemSolicitado item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "O item solicitado é obrigatório."
            );
        }

        if (!oferta.estaPendente()) {
            throw new RegraDeNegocioException(
                    "Apenas ofertas pendentes podem receber itens."
            );
        }

        itensSolicitados.add(item);
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
}