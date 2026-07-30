package br.com.marketplace.entity.id;

import br.com.marketplace.entity.enums.TipoFigurinha;
import jakarta.persistence.*;
import lombok.*;
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
    @Column(name = "tipo")
    private TipoFigurinha tipo;
}
