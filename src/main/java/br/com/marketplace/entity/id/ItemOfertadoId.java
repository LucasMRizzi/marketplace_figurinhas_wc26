package br.com.marketplace.entity.id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemOfertadoId implements Serializable {

    private Long idItem;
    private Long oferta;
}