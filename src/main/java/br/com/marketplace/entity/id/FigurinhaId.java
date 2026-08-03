package br.com.marketplace.entity.id;

import br.com.marketplace.entity.enums.TipoFigurinha;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FigurinhaId implements Serializable {

    @Column(name = "codigo", length = 6)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "tipo",
            nullable = false,
            columnDefinition = "tipo_figurinha"
    )
    private TipoFigurinha tipo;
}
