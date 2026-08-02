package br.com.marketplace.dto.itemOfertado;

import br.com.marketplace.entity.enums.Condicao;

public record ItemOfertadoResponse(
        Integer idItem,
        Integer idOferta,
        Integer idPosse,
        String nomeFigurinha,
        Integer quantidadeOfertada,
        Condicao condicao,
        String foto
) {
}

/** Exemplo de arquivo json:
 * {
  "idItem": 300,
  "idOferta": 45,
  "idPosse": 150,
  "nomeFigurinha": "Vinícius Júnior",
  "quantidadeOfertada": 1,
  "condicao": "COM_MARCAS_DE_USO",
  "foto": "https://meubucket.com/fotos/figurinha-bra10-nova.jpg"
  }
 */