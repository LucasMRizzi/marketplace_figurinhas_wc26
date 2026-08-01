package br.com.marketplace.entity;

import br.com.marketplace.entity.id.DesejaFigurinhaId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "deseja_figurinha")
public class DesejaFigurinha {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @EmbeddedId
    private DesejaFigurinhaId id;

    /**
     * =========================================================
     * Chaves Estrangeiras
     * =========================================================
     */

    @MapsId("figurinhaId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(
                    name = "codigo_da_figurinha",
                    referencedColumnName = "codigo"
            ),
            @JoinColumn(
                    name = "tipo_da_figurinha",
                    referencedColumnName = "tipo"
            )
    })
    private Figurinha figurinha;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario",
            referencedColumnName = "cpf"
    )
    private Usuario usuario;

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public DesejaFigurinha(
            Usuario usuario,
            Figurinha figurinha
    ) {
        this.usuario = usuario;
        this.figurinha = figurinha;

        this.id = new DesejaFigurinhaId(
                figurinha.getCodigo(),
                figurinha.getTipo(),
                usuario.getCpf()
        );
    }
}
