package br.com.marketplace.mapper;

import br.com.marketplace.dto.troca.CriarTrocaRequest;
import br.com.marketplace.dto.troca.TrocaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Troca;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.enums.TipoOferta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class TrocaMapper {


    public Troca toEntity(
            CriarTrocaRequest request,
            Oferta oferta
    ) {

        return new Troca(
                oferta
        );
    }

    public TrocaResponse toResponse(Troca troca) {
        Oferta oferta = troca.getOferta();

        return new TrocaResponse(
                oferta.getIdOferta(),
                oferta.getStatus(),
                oferta.getDataCriacao(),
                oferta.getPrazoLimite(),
                oferta.getDescricao(),
                oferta.getValorDeMercado(),
                oferta.getUsuarioProponente().getCpf()
        );
    }
}