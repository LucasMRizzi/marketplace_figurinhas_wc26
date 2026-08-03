package br.com.marketplace.mapper;

import br.com.marketplace.dto.avaliacao.AvaliacaoResponse;
import br.com.marketplace.dto.avaliacao.CriarAvaliacaoRequest;
import br.com.marketplace.entity.Avaliacao;
import br.com.marketplace.entity.Concretizacao;
import br.com.marketplace.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoMapper {

    public Avaliacao toEntity(
            CriarAvaliacaoRequest request,
            Concretizacao concretizacao,
            Usuario avaliador,
            Usuario avaliado
    ) {
        return new Avaliacao(
                avaliador,
                avaliado,
                concretizacao,
                request.nota(),
                request.comentario()
        );
    }

    public AvaliacaoResponse toResponse(
            Avaliacao avaliacao
    ) {
        return new AvaliacaoResponse(
                avaliacao.getConcretizacao()
                        .getIdConcretizacao(),
                avaliacao.getUsuarioAvaliador().getCpf(),
                avaliacao.getUsuarioAvaliador().getNome(),
                avaliacao.getUsuarioAvaliado().getCpf(),
                avaliacao.getUsuarioAvaliado().getNome(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getData()
        );
    }
}