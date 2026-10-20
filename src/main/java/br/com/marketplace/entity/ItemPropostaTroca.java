package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.Condicao;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "item_proposta_troca")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPropostaTroca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_proposta")
    private Integer idItemProposta;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "condicao", nullable = false, columnDefinition = "condicao")
    private Condicao condicao;

    @Column(name = "foto", length = 255)
    private String foto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_proposta", nullable = false)
    private PropostaTroca proposta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_posse", nullable = false)
    private PosseFigurinha posse;

    public ItemPropostaTroca(PropostaTroca proposta, PosseFigurinha posse,
                             Integer quantidade, Condicao condicao, String foto) {
        if (proposta == null || posse == null || condicao == null) {
            throw new IllegalArgumentException("Proposta, posse e condição são obrigatórias.");
        }
        if (quantidade == null || quantidade <= 0 || quantidade > posse.getQuantidade()) {
            throw new IllegalArgumentException("A quantidade proposta é inválida.");
        }
        if (foto != null && foto.length() > 255) {
            throw new IllegalArgumentException("A foto deve possuir no máximo 255 caracteres.");
        }
        this.proposta = proposta;
        this.posse = posse;
        this.quantidade = quantidade;
        this.condicao = condicao;
        this.foto = foto;
    }
}
