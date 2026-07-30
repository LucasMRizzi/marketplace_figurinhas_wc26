package br.com.marketplace.entity;

import br.com.marketplace.entity.id.FigurinhaId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "figurinha")
public class Figurinha {

    @EmbeddedId
    private FigurinhaId id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(
            name = "valor_de_mercado",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal valor_de_mercado;

    protected Figurinha(){
    }

    public Figurinha(
            FigurinhaId id,
            String nome
    ) {
        this.id = id;
        this.nome = nome;
        this.valor_de_mercado = BigDecimal.ZERO;
    }
}
