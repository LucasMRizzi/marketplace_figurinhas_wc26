package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.TipoOferta;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "venda")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venda {

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
            BigDecimal valorDaProposta
    ) {
        validarOferta(oferta);
        validarValor(valorDaProposta, "valor da proposta");

        this.oferta = oferta;
        this.valorDaProposta = valorDaProposta;
    }

    public void atualizarVenda(
            BigDecimal valorDaProposta
    ) {
        validarValor(valorDaProposta, "valor da proposta");

        this.valorDaProposta = valorDaProposta;
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

        if (!oferta.ehVenda()) {
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