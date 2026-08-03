package br.com.marketplace.mapper;

import br.com.marketplace.dto.posseFigurinha.CriarPosseFigurinhaRequest;
import br.com.marketplace.dto.posseFigurinha.PosseFigurinhaResponse;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.PosseFigurinha;
import br.com.marketplace.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class PosseFigurinhaMapper {

    public PosseFigurinha toEntity(
            Usuario usuario,
            Figurinha figurinha,
            CriarPosseFigurinhaRequest request
    ) {
        return new PosseFigurinha(
                usuario,
                figurinha,
                request.quantidade()
        );
    }

    public PosseFigurinhaResponse toResponse(
            PosseFigurinha posse_figurinha
    ){
        return new PosseFigurinhaResponse(
                posse_figurinha.getIdPosse(),
                posse_figurinha.getUsuario().getCpf(),
                posse_figurinha.getFigurinha().getCodigo(),
                posse_figurinha.getFigurinha().getTipo(),
                posse_figurinha.getQuantidade()
        );
    }
}
