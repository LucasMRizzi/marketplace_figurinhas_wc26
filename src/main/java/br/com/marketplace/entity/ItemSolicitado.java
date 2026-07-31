package br.com.marketplace.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "item_solicitado"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSolicitado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_solicitado")
    private Integer idItemSolicitado;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(
                    name = "codigo_da_figurinha",
                    referencedColumnName = "codigo",
                    nullable = false
            ),
            @JoinColumn(
                    name = "tipo_da_figurinha",
                    referencedColumnName = "tipo",
                    nullable = false
            )
    })
    private Figurinha figurinha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_oferta",
            referencedColumnName = "id_oferta",
            nullable = false
    )
    private Troca troca;

    public ItemSolicitado(
            Troca troca,
            Figurinha figurinha,
            Integer quantidade
    ) {
        if (troca == null) {
            throw new IllegalArgumentException(
                    "A troca é obrigatória."
            );
        }

        if (figurinha == null) {
            throw new IllegalArgumentException(
                    "A figurinha é obrigatória."
            );
        }

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        this.troca = troca;
        this.figurinha = figurinha;
        this.quantidade = quantidade;
    }

    public void alterarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        this.quantidade = quantidade;
    }
}