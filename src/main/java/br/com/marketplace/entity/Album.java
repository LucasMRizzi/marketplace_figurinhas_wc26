package br.com.marketplace.entity;

import br.com.marketplace.entity.id.AlbumId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "album")
public class Album {

    private static final BigDecimal COMPLETUDE_MINIMA =
            BigDecimal.ZERO;

    private static final BigDecimal COMPLETUDE_MAXIMA =
            new BigDecimal("100.00");

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @EmbeddedId
    private AlbumId id;

    @Column(
            name = "completude",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal completude;

    /**
     * =========================================================
     * Chaves Estrangeiras
     * =========================================================
     */

    @MapsId("usuario")
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "usuario",
            referencedColumnName = "cpf",
            nullable = false
    )
    private Usuario usuario;

    /**
     * =========================================================
     * Relações
     * =========================================================
     */

    @OneToMany(mappedBy = "album")
    private List<FigurinhaColada> figurinhasColadas = new ArrayList<>();

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public Album(
            String nome,
            Usuario usuario
    ) {
        validarNome(nome);

        if(usuario == null){
            throw new IllegalArgumentException(
                    "O usuário é obrigatório."
            );
        }

        this.id = new AlbumId(
                nome.trim(),
                usuario.getCpf()
        );

        this.usuario = usuario;
        this.completude = BigDecimal.ZERO;
    }

    public void atualizarCompletude(
            BigDecimal novaCompletude
    ) {
        validarCompletude(novaCompletude);

        this.completude = novaCompletude.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

    private void validarNome(String nome){
        if(nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do álbum é obrigatório."
            );
        }

        if(nome.length() > 100) {
            throw new IllegalArgumentException(
                    "O nome do álbum deve ter no máximo 100 caracteres."
            );
        }
    }

    private void validarCompletude(BigDecimal completude) {
        if(completude == null) {
            throw new IllegalArgumentException(
                    "A completude é obrigatória."
            );
        }

        if(completude.compareTo(COMPLETUDE_MINIMA) < 0
                || completude.compareTo(COMPLETUDE_MAXIMA) > 0) {
            throw new IllegalArgumentException(
                    "A completude deve estar entre 0 e 100."
            );
        }
    }

}
