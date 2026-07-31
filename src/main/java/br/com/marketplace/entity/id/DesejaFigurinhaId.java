package br.com.marketplace.entity.id;

import br.com.marketplace.entity.enums.TipoFigurinha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DesejaFigurinhaId implements Serializable {

    @Column(
            name = "codigo_da_figurinha",
            nullable = false,
            length = 6
    )
    private String codigoFigurinha;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "tipo_da_figurinha",
            nullable = false,
            columnDefinition = "tipo_figurinha"
    )
    private TipoFigurinha tipoFigurinha;


    @Column(
            name = "usuario",
            nullable = false,
            length = 14
    )
    private String usuarioId;
}
