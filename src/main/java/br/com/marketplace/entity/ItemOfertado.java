package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.Condicao;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "item_ofertado")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOfertado {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Integer idItem;

    @Column(name = "foto", length = 255)
    private String foto;

    @Column(
            name = "quantidade_ofertada",
            nullable = false
    )
    private Integer quantidadeOfertada;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "condicao",
            nullable = false,
            columnDefinition = "condicao"
    )
    private Condicao condicao;

    /**
     * =========================================================
     * Chaves Estrangeiras
     * =========================================================
     */

    /**
     * Chave para tabela oferta
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_oferta",
            referencedColumnName = "id_oferta",
            nullable = false
    )
    private Oferta oferta;

    /**
     * Chave para tabela possui_figurinha
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_posse",
            referencedColumnName = "id_posse",
            nullable = false
    )
    private PosseFigurinha posseFigurinha;

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public ItemOfertado(
            Oferta oferta,
            PosseFigurinha posseFigurinha,
            Integer quantidadeOfertada,
            Condicao condicao,
            String foto
    ) {
        validarOferta(oferta);
        validarPosse(posseFigurinha);
        validarQuantidade(quantidadeOfertada, posseFigurinha);
        validarCondicao(condicao);

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
        validarQuantidade(quantidadeOfertada, this.posseFigurinha);
        validarCondicao(condicao);
        validarFoto(foto);

        this.quantidadeOfertada = quantidadeOfertada;
        this.condicao = condicao;
        this.foto = foto;
    }

    public Integer getIdOferta(){
        return this.oferta.getIdOferta();
    }

    public Integer getIdPosse(){
        return this.posseFigurinha.getIdPosse();
    }

    public String getNomeFigurinha(){
        return this.posseFigurinha.getNomeFigurinha();
    }

    public BigDecimal calcularValorDeMercado() {
        BigDecimal valorUnitario =
                posseFigurinha
                        .getFigurinha()
                        .getValorDeMercado();

        return valorUnitario.multiply(
                BigDecimal.valueOf(quantidadeOfertada)
        );
    }
    /**
     * =========================================================
     * Métodos auxiliares
     * =========================================================
     */

    private void validarCondicao(Condicao condicao){
        if (condicao == null) {
            throw new IllegalArgumentException(
                    "A condição é obrigatória."
            );
        }
    }

    private void validarOferta(Oferta oferta){
        if (oferta == null) {
            throw new IllegalArgumentException(
                    "A oferta é obrigatória."
            );
        }
    }

    private void validarPosse(PosseFigurinha posseFigurinha){
        if (posseFigurinha == null) {
            throw new IllegalArgumentException(
                    "A posse da figurinha é obrigatória."
            );
        }
    }

    private void validarQuantidade(Integer quantidadeOfertada, PosseFigurinha posseFigurinha){
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
    }

    private void validarFoto(String foto){
        if (foto != null && foto.length() > 255) {
            throw new IllegalArgumentException(
                    "A foto deve possuir no máximo 255 caracteres."
            );
        }
    }
}