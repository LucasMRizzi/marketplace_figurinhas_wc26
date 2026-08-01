package br.com.marketplace.mapper;

import br.com.marketplace.dto.venda.CriarVendaRequest;
import br.com.marketplace.dto.venda.VendaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Venda;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VendaMapper {

    public Venda toEntity(
            CriarVendaRequest request,
            Oferta oferta
    ) {

        return new Venda(
                oferta,
                request.valorDaProposta()
        );
    }

    public VendaResponse toResponse(Venda venda) {
        Oferta oferta = venda.getOferta();

        return new VendaResponse(
                venda.getIdOferta(),
                oferta.getStatus(),
                oferta.getDataCriacao(),
                oferta.getPrazoLimite(),
                oferta.getDescricao(),
                oferta.getValorDeMercado(),
                oferta.getUsuarioProponente().getCpf(),
                venda.getValorDaProposta()
        );
    }
}