package br.com.marketplace.mapper;

import br.com.marketplace.dto.propostaTroca.PropostaTrocaResponse;
import br.com.marketplace.entity.ItemPropostaTroca;
import br.com.marketplace.entity.PropostaTroca;
import org.springframework.stereotype.Component;

@Component
public class PropostaTrocaMapper {
    public PropostaTrocaResponse toResponse(PropostaTroca proposta) {
        return new PropostaTrocaResponse(
                proposta.getIdProposta(), proposta.getOferta().getIdOferta(), proposta.getStatus(),
                proposta.getDataCriacao(), proposta.getDataResposta(),
                proposta.getSolicitante().getCpf(), proposta.getSolicitante().getNome(),
                proposta.getSolicitante().getEndereco() == null ? null : proposta.getSolicitante().getEndereco().getCidade(),
                proposta.getSolicitante().getAvaliacaoMedia(),
                proposta.getOferta().getUsuarioProponente().getCpf(),
                proposta.getOferta().getUsuarioProponente().getNome(),
                proposta.getOferta().getValorDeMercado(),
                proposta.getItens().stream()
                        .map(item -> item.getPosse().getFigurinha().getValorDeMercado()
                                .multiply(java.math.BigDecimal.valueOf(item.getQuantidade())))
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add),
                proposta.getItens().stream().map(this::toItemResponse).toList()
        );
    }

    private PropostaTrocaResponse.ItemPropostaResponse toItemResponse(ItemPropostaTroca item) {
        return new PropostaTrocaResponse.ItemPropostaResponse(
                item.getIdItemProposta(), item.getPosse().getIdPosse(),
                item.getPosse().getFigurinha().getCodigo(), item.getPosse().getNomeFigurinha(),
                item.getPosse().getFigurinha().getTipo().name(), item.getQuantidade(),
                item.getCondicao().name(), item.getFoto(),
                item.getPosse().getFigurinha().getValorDeMercado(),
                item.getPosse().getFigurinha().getValorDeMercado()
                        .multiply(java.math.BigDecimal.valueOf(item.getQuantidade()))
        );
    }
}
