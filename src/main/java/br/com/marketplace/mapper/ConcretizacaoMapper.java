package br.com.marketplace.mapper;

import br.com.marketplace.dto.concretizacao.ConcretizacaoResponse;
import br.com.marketplace.entity.Concretizacao;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class ConcretizacaoMapper {

    public Concretizacao toEntity(
            Oferta oferta,
            Usuario aceitante
    ) {
        return new Concretizacao(
                oferta,
                aceitante
        );
    }

    public ConcretizacaoResponse toResponse(
            Concretizacao concretizacao
    ) {
        Oferta oferta = concretizacao.getOferta();

        return new ConcretizacaoResponse(
                concretizacao.getIdConcretizacao(),
                oferta.getIdOferta(),
                concretizacao.getStatusPagamento(),
                concretizacao.getDataAceite(),
                concretizacao.getAceitante().getCpf(),
                concretizacao.getAceitante().getNome(),
                oferta.getUsuarioProponente().getCpf()
        );
    }
}