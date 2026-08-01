package br.com.marketplace.entity;

import br.com.marketplace.entity.id.FigurinhaColadaId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "figurinhas_coladas")
public class FigurinhaColada {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @EmbeddedId
    private FigurinhaColadaId id;

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

    @MapsId("albumId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(
                    name = "nome_do_album",
                    referencedColumnName = "nome",
                    nullable = false
            ),
            @JoinColumn(
                    name = "usuario",
                    referencedColumnName = "usuario",
                    nullable = false
            )
    })
    private Album album;

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public FigurinhaColada(
            Album album,
            Figurinha figurinha
    ) {
        if (album == null) {
            throw new IllegalArgumentException(
                    "O álbum é obrigatório."
            );
        }

        if (figurinha == null) {
            throw new IllegalArgumentException(
                    "A figurinha é obrigatória."
            );
        }

        this.album = album;
        this.figurinha = figurinha;

        this.id = new FigurinhaColadaId(
                figurinha.getCodigo(),
                figurinha.getTipo(),
                album.getId()
        );
    }
}
