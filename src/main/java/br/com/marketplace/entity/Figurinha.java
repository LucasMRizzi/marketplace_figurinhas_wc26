package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.id.FigurinhaId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "figurinha")
public class Figurinha {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

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
    private BigDecimal valorDeMercado;

    /**
     * =========================================================
     * Relações
     * =========================================================
     */

    @OneToMany(mappedBy = "figurinha")
    private List<PosseFigurinha> posses = new ArrayList<>();

    @OneToMany(mappedBy = "figurinha")
    private List<DesejaFigurinha> desejos = new ArrayList<>();

    @OneToMany(mappedBy = "figurinha")
    private List<FigurinhaColada> coladas = new ArrayList<>();

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public Figurinha(
            FigurinhaId id,
            String nome
    ) {
        this.id = id;
        this.nome = nome;
        this.valorDeMercado = BigDecimal.ZERO;
    }

    public void atualizarDados(
            String nome,
            BigDecimal valorDeMercado
    ) {
        if(nome == null || nome.isBlank()){
            throw new IllegalArgumentException(
                    "O nome é obrigatório."
            );
        }

        if(valorDeMercado == null){
            throw new IllegalArgumentException(
                    "O valor de mercado é obrigatório."
            );
        }

        if(valorDeMercado.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException(
                    "O valor de mercado não pode ser menor que 0."
            );
        }

        this.nome = nome;
        this.valorDeMercado = valorDeMercado;
    }

    public String getCodigo(){
        return id.getCodigo();
    }

    public TipoFigurinha getTipo(){
        return id.getTipo();
    }

}
