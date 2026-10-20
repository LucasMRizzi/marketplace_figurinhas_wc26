package br.com.marketplace.dto.propostaTroca;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CriarPropostaTrocaRequest(
        @NotEmpty(message = "Selecione pelo menos uma figurinha para propor.")
        List<@Valid CriarItemOfertadoRequest> itens
) {}
