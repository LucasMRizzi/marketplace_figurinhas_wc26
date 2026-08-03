package br.com.marketplace.mapper;

import br.com.marketplace.dto.itemOfertado.AtualizarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.ItemOfertadoResponse;
import br.com.marketplace.entity.ItemOfertado;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.PosseFigurinha;
import org.springframework.stereotype.Component;

@Component
public class ItemOfertadoMapper {

    public ItemOfertado toEntity(
            CriarItemOfertadoRequest request,
            Oferta oferta,
            PosseFigurinha posseFigurinha
    ) {
        return new ItemOfertado(
                oferta,
                posseFigurinha,
                request.quantidadeOfertada(),
                request.condicao(),
                request.foto()
        );
    }

    public ItemOfertadoResponse toResponse(ItemOfertado item) {
        return new ItemOfertadoResponse(
                item.getIdItem(),
                item.getIdOferta(),
                item.getIdPosse(),
                item.getNomeFigurinha(),
                item.getQuantidadeOfertada(),
                item.getCondicao(),
                item.getFoto()
        );
    }

    public void updateEntity(
            ItemOfertado item,
            AtualizarItemOfertadoRequest request
    ) {
        item.atualizar(
                request.quantidadeOfertada(),
                request.condicao(),
                request.foto()
        );
    }
}