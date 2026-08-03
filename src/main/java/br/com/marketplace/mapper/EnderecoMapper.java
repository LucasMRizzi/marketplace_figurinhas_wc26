package br.com.marketplace.mapper;

import br.com.marketplace.dto.endereco.EnderecoRequest;
import br.com.marketplace.dto.endereco.EnderecoResponse;
import br.com.marketplace.entity.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    public Endereco toEntity(EnderecoRequest request){
        if(request == null){
            return null;
        }

        return new Endereco(
                request.logradouro(),
                request.numero(),
                request.caixaPostal(),
                request.cidade(),
                request.cep()
        );
    }

    public EnderecoResponse toResponse(
            Endereco endereco
    ) {
        if(endereco == null){
            return null;
        }

        return new EnderecoResponse(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getCaixaPostal(),
                endereco.getCidade(),
                endereco.getCep()
        );
    }
}
