package br.com.marketplace.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AlbumId implements Serializable {

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "usuario", nullable = false, length = 14)
    private String usuario;
}
