package br.com.marketplace.entity;

import br.com.marketplace.entity.id.DesejaFigurinhaId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "deseja_figurinha")
public class DesejaFigurinha {

    @EmbeddedId
    private DesejaFigurinhaId id;

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

    protected DesejaFigurinha(){
    }

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
