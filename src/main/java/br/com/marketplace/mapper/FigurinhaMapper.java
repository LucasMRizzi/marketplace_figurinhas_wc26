package br.com.marketplace.mapper;

import br.com.marketplace.dto.figurinha.AtualizarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.CriarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.FigurinhaResponse;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.id.FigurinhaId;
import org.springframework.stereotype.Component;


@Component
public class FigurinhaMapper {

    public Figurinha toEntity(CriarFigurinhaRequest request){
        return new Figurinha(
                new FigurinhaId(
                        request.codigo(),
                        request.tipo()
                ),
                request.nome()

        );
    }

    public FigurinhaResponse toResponse(
            Figurinha figurinha
    ) {
        return new FigurinhaResponse(
                figurinha.getId().getCodigo(),
                figurinha.getId().getTipo(),
                figurinha.getNome(),
                figurinha.getValorDeMercado()
        );
    }

    public void updateEntity(
            Figurinha figurinha,
            AtualizarFigurinhaRequest request
    ) {
        figurinha.atualizarDados(
                request.nome(),
                request.valorDeMercado()
        );
    }
}
