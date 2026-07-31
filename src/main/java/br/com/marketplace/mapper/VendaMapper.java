package br.com.marketplace.mapper;

import br.com.marketplace.dto.venda.CriarVendaRequest;
import br.com.marketplace.dto.venda.VendaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.Venda;
import br.com.marketplace.entity.enums.TipoOferta;
import org.springframework.stereotype.Component;

@Component
public class VendaMapper {

    public Venda toEntity(
            CriarVendaRequest request,
            Usuario proponente
    ) {
        Oferta oferta = new Oferta(
                TipoOferta.VENDA,
                proponente
        );

        return new Venda(
                oferta,
                request.precoUnitario(),
                request.quantidade()
        );
    }

    public VendaResponse toResponse(Venda venda) {
        Oferta oferta = venda.getOferta();

        return new VendaResponse(
                oferta.getIdOferta(),
                oferta.getStatus(),
                oferta.getTipo(),
                oferta.getDataCriacao(),
                oferta.getUsuarioProponente().getCpf(),
                venda.getValorDaProposta(),
                venda.getPrecoUnitario(),
                venda.getQuantidade()
        );
    }
}