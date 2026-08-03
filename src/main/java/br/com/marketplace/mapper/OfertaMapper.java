package br.com.marketplace.mapper;

import br.com.marketplace.dto.oferta.OfertaResponse;
import br.com.marketplace.entity.Oferta;
import org.springframework.stereotype.Component;

@Component
public class OfertaMapper {

    public OfertaResponse toResponse(Oferta oferta) {

        return new OfertaResponse(
                oferta.getIdOferta(),
                oferta.getStatus(),
                oferta.getTipo(),
                oferta.getDataCriacao(),
                oferta.getPrazoLimite(),
                oferta.getDescricao(),
                oferta.getValorDeMercado(),
                oferta.getUsuarioProponente().getCpf()
        );
    }
}