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
                oferta.getUsuarioProponente().getCpf(),
                oferta.getUsuarioProponente().getNome(),
                oferta.getUsuarioProponente().getEndereco() == null ? null
                        : oferta.getUsuarioProponente().getEndereco().getCidade(),
                oferta.getUsuarioProponente().getAvaliacaoMedia(),
                oferta.getVenda() == null ? null : oferta.getVenda().getValorDaProposta(),
                oferta.getItensOfertados().stream().map(item -> new OfertaResponse.ItemCardResponse(
                        item.getIdItem(), item.getPosseFigurinha().getFigurinha().getCodigo(),
                        item.getNomeFigurinha(), item.getPosseFigurinha().getFigurinha().getTipo().name(),
                        item.getQuantidadeOfertada(), item.getCondicao().name(), item.getFoto(),
                        item.getPosseFigurinha().getFigurinha().getValorDeMercado(),
                        item.calcularValorDeMercado())).toList(),
                oferta.getTroca() == null ? java.util.List.of()
                        : oferta.getTroca().getItensSolicitados().stream()
                        .map(item -> new OfertaResponse.ItemSolicitadoResponse(
                                item.getFigurinha().getCodigo(), item.getFigurinha().getNome(),
                                item.getFigurinha().getTipo().name(), item.getQuantidade()))
                        .toList()
        );
    }
}
