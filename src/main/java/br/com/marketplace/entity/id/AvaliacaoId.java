package br.com.marketplace.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AvaliacaoId implements Serializable {

    @Column(
            name = "usuario_avaliador",
            length = 14
    )
    private String cpfAvaliador;

    @Column(
            name = "usuario_avaliado",
            length = 14
    )
    private String cpfAvaliado;

    @Column(name = "id_concretizacao")
    private Integer idConcretizacao;
}