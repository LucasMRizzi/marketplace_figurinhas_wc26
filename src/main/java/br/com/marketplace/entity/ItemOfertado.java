package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.Condicao;
import br.com.marketplace.entity.id.ItemOfertadoId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "item_ofertado")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOfertado {

    @EmbeddedId
    private ItemOfertadoId id;

    @MapsId("idOferta")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_oferta",
            referencedColumnName = "id_oferta",
            nullable = false
    )
    private Oferta oferta;

    @Column(name = "foto", length = 255)
    private String foto;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "condicao",
            nullable = false,
            columnDefinition = "condicao"
    )
    private Condicao condicao;

    @Column(
            name = "quantidade_ofertada",
            nullable = false
    )
    private Integer quantidadeOfertada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_posse",
            referencedColumnName = "id_posse",
            nullable = false
    )
    private PosseFigurinha posseFigurinha;

    public ItemOfertado(
            Oferta oferta,
            PosseFigurinha posseFigurinha,
            Integer quantidadeOfertada,
            Condicao condicao,
            String foto
    ) {
        if (oferta == null) {
            throw new IllegalArgumentException(
                    "A oferta é obrigatória."
            );
        }

        if (posseFigurinha == null) {
            throw new IllegalArgumentException(
                    "A posse da figurinha é obrigatória."
            );
        }

        if (quantidadeOfertada == null
                || quantidadeOfertada <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade ofertada deve ser maior que zero."
            );
        }

        if (quantidadeOfertada
                > posseFigurinha.getQuantidade()) {
            throw new IllegalArgumentException(
                    "A quantidade ofertada é maior que a quantidade possuída."
            );
        }

        if (condicao == null) {
            throw new IllegalArgumentException(
                    "A condição é obrigatória."
            );
        }

        this.oferta = oferta;
        this.posseFigurinha = posseFigurinha;
        this.quantidadeOfertada = quantidadeOfertada;
        this.condicao = condicao;
        this.foto = foto;
    }
    public void atualizar(
            Integer quantidadeOfertada,
            Condicao condicao,
            String foto
    ) {
        if (quantidadeOfertada == null || quantidadeOfertada <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade ofertada deve ser maior que zero."
            );
        }

        if (quantidadeOfertada > posseFigurinha.getQuantidade()) {
            throw new IllegalArgumentException(
                    "A quantidade ofertada é maior que a quantidade possuída."
            );
        }

        if (condicao == null) {
            throw new IllegalArgumentException(
                    "A condição é obrigatória."
            );
        }

        if (foto != null && foto.length() > 255) {
            throw new IllegalArgumentException(
                    "A foto deve possuir no máximo 255 caracteres."
            );
        }

        this.quantidadeOfertada = quantidadeOfertada;
        this.condicao = condicao;
        this.foto = foto;
    }
}