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

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_solicitado")
    private Integer idItemSolicitado;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    /**
     * =========================================================
     * Chaves Estrangeiras
     * =========================================================
     */

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

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public ItemSolicitado(
            Troca troca,
            Figurinha figurinha,
            Integer quantidade
    ) {

        validarTroca(troca);
        validarFigurinha(figurinha);
        validarQuantidade(quantidade);

        this.troca = troca;
        this.figurinha = figurinha;
        this.quantidade = quantidade;
    }

    public void alterarQuantidade(Integer quantidade) {

        validarQuantidade(quantidade);

        this.quantidade = quantidade;
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

    private void validarQuantidade(Integer quantidade){
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }
    }

    private void validarFigurinha(Figurinha figurinha){
        if (figurinha == null) {
            throw new IllegalArgumentException(
                    "A figurinha é obrigatória."
            );
        }
    }

    private void validarTroca(Troca troca){
        if (troca == null) {
            throw new IllegalArgumentException(
                    "A troca é obrigatória."
            );
        }
    }

}