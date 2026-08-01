package br.com.marketplace.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "possui_figurinha")
public class PosseFigurinha {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_posse")
    private Integer idPosse;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    /**
     * =========================================================
     * Chaves Estrangeiras
     * =========================================================
     */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario",
            referencedColumnName = "cpf",
            nullable = false
    )
    private Usuario usuario;

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

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public PosseFigurinha(
            Usuario usuario,
            Figurinha figurinha,
            Integer quantidade
    ) {
        this.usuario = usuario;
        this.figurinha = figurinha;
        alterarQuantidade(quantidade);
    }

    public void adicionarQuantidade(int quantidade){
        validarQuantidade(quantidade);

        this.quantidade += quantidade;
    }

    public void removerQuantidade(int quantidade){
        validarQuantidade(quantidade);
        validarQuantidadeSuficiente(quantidade);

        this.quantidade -= quantidade;
    }

    public void alterarQuantidade(int quantidade) {
        validarQuantidade(quantidade);

        this.quantidade = quantidade;
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

    private void validarQuantidade(Integer quantidade){
        if (quantidade < 0) {
            throw new IllegalArgumentException(
                    "A quantidade não pode ser negativa"
            );
        }
    }

    private void validarQuantidadeSuficiente(Integer quantidade){
        if(quantidade > this.quantidade){
            throw new IllegalArgumentException(
                    "Quantidade insuficiente de figurinhas"
            );
        }
    }
}
