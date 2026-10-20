package br.com.marketplace.dto.propostaTroca;

import br.com.marketplace.entity.enums.StatusPropostaTroca;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public record PropostaTrocaResponse(
        Integer idProposta,
        Integer idOferta,
        StatusPropostaTroca status,
        LocalDateTime dataCriacao,
        LocalDateTime dataResposta,
        String cpfSolicitante,
        String nomeSolicitante,
        String cidadeSolicitante,
        BigDecimal avaliacaoSolicitante,
        String cpfProponente,
        String nomeProponente,
        BigDecimal valorMercadoOferta,
        BigDecimal valorMercadoProposta,
        List<ItemPropostaResponse> itens
) {
    public record ItemPropostaResponse(
            Integer idItem, Integer idPosse, String codigo, String nome,
            String raridade, Integer quantidade, String condicao, String foto,
            BigDecimal valorUnitario, BigDecimal valorTotal
    ) {}
}
