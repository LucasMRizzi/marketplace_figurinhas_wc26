package br.com.marketplace.mapper;

import br.com.marketplace.dto.itemSolicitado.AtualizarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.ItemSolicitadoResponse;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.ItemSolicitado;
import br.com.marketplace.entity.Troca;
import org.springframework.stereotype.Component;

@Component
public class ItemSolicitadoMapper {

    public ItemSolicitado toEntity(
            CriarItemSolicitadoRequest request,
            Troca troca,
            Figurinha figurinha
    ) {
        return new ItemSolicitado(
                troca,
                figurinha,
                request.quantidade()
        );
    }

    public ItemSolicitadoResponse toResponse(
            ItemSolicitado item
    ) {
        return new ItemSolicitadoResponse(
                item.getIdItemSolicitado(),
                item.getTroca().getIdOferta(),
                item.getFigurinha().getId().getCodigo(),
                item.getFigurinha().getId().getTipo(),
                item.getFigurinha().getNome(),
                item.getQuantidade()
        );
    }

    public void updateEntity(
            ItemSolicitado item,
            AtualizarItemSolicitadoRequest request
    ) {
        item.alterarQuantidade(request.quantidade());
    }
}