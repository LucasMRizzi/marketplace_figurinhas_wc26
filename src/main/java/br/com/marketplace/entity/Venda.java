package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.TipoOferta;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "venda")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venda {

    //TODO: atualizar preco unitario e quantidade para o dos itens ofertados

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Id
    @Column(name = "id_oferta")
    private Integer idOferta;

    @Column(
            name = "valor_da_proposta",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal valorDaProposta;

    @Column(
            name = "preco_unitario",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precoUnitario;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    /**
     * =========================================================
     * Chave Estrangeira
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
     * Métodos
     * =========================================================
     */

    public Venda(
            Oferta oferta,
            BigDecimal precoUnitario,
            Integer quantidade
    ) {
        validarOferta(oferta);
        validarValor(precoUnitario, "preço unitário");

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        this.oferta = oferta;
        this.valorDaProposta = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }

    public void atualizarVenda(
            BigDecimal precoUnitario,
            Integer quantidade
    ) {
        validarValor(precoUnitario, "preço unitário");
        this.precoUnitario = precoUnitario;
        this.valorDaProposta = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        this.quantidade = quantidade;
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

        if (oferta.getTipo() != TipoOferta.VENDA) {
            throw new IllegalArgumentException(
                    "A oferta deve ser do tipo venda."
            );
        }
    }

    private void validarValor(
            BigDecimal valor,
            String campo
    ) {
        if (valor == null
                || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O " + campo + " não pode ser negativo."
            );
        }
    }


}