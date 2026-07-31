package br.com.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "possui_figurinha")
public class PosseFigurinha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_posse")
    private Integer idPosse;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

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

    protected PosseFigurinha(){

    }

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
        if(quantidade <= 0){
            throw new IllegalArgumentException(
                    "A quantidade adicionada deve ser maior que zero"
            );
        }

        this.quantidade += quantidade;
    }

    public void removerQuantidade(int quantidade){
        if(quantidade <= 0){
            throw new IllegalArgumentException(
                    "A quantidade adicionada deve ser maior que zero"
            );
        }
        if(quantidade > this.quantidade){
            throw new IllegalArgumentException(
                    "Quantidade insuficiente de figurinhas"
            );
        }

        this.quantidade -= quantidade;
    }

    public void alterarQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException(
                    "A quantidade não pode ser negativa"
            );
        }

        this.quantidade = quantidade;
    }
}
