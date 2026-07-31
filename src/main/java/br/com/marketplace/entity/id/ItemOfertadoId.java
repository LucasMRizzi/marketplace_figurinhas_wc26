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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemOfertadoId implements Serializable {

    @Column(name = "id_item")
    private Integer idItem;

    @Column(name = "id_oferta")
    private Integer idOferta;
}