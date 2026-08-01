package br.com.marketplace.service;

import br.com.marketplace.dto.itemSolicitado.AtualizarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import br.com.marketplace.dto.itemSolicitado.ItemSolicitadoResponse;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.ItemSolicitado;
import br.com.marketplace.entity.Troca;
import br.com.marketplace.entity.id.FigurinhaId;
import br.com.marketplace.mapper.ItemSolicitadoMapper;
import br.com.marketplace.repository.FigurinhaRepository;
import br.com.marketplace.repository.ItemSolicitadoRepository;
import br.com.marketplace.repository.TrocaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemSolicitadoService {

    private final ItemSolicitadoRepository itemSolicitadoRepository;
    private final TrocaRepository trocaRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final ItemSolicitadoMapper itemSolicitadoMapper;

    @Transactional
    public ItemSolicitadoResponse criar(
            Integer idOferta,
            CriarItemSolicitadoRequest request
    ) {
        Troca troca = buscarTroca(idOferta);

        validarTrocaPendente(troca);

        if (itemSolicitadoRepository.existeFigurinhaNaTroca(
                idOferta,
                request.codigoFigurinha(),
                request.tipoFigurinha()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Essa figurinha já foi solicitada nessa troca."
            );
        }

        FigurinhaId figurinhaId = new FigurinhaId(
                request.codigoFigurinha(),
                request.tipoFigurinha()
        );

        Figurinha figurinha = figurinhaRepository
                .findById(figurinhaId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Figurinha não encontrada."
                        )
                );

        ItemSolicitado item =
                itemSolicitadoMapper.toEntity(
                        request,
                        troca,
                        figurinha
                );

        ItemSolicitado salvo =
                itemSolicitadoRepository.save(item);

        return itemSolicitadoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ItemSolicitadoResponse> listarPorTroca(
            Integer idOferta
    ) {
        buscarTroca(idOferta);

        return itemSolicitadoRepository
                .buscarTodosPorTroca(idOferta)
                .stream()
                .map(itemSolicitadoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemSolicitadoResponse buscar(
            Integer idOferta,
            Integer idItemSolicitado
    ) {
        return itemSolicitadoMapper.toResponse(
                buscarEntidade(idOferta, idItemSolicitado)
        );
    }

    @Transactional
    public ItemSolicitadoResponse atualizar(
            Integer idOferta,
            Integer idItemSolicitado,
            AtualizarItemSolicitadoRequest request
    ) {
        ItemSolicitado item = buscarEntidade(
                idOferta,
                idItemSolicitado
        );

        validarTrocaPendente(item.getTroca());

        itemSolicitadoMapper.updateEntity(item, request);

        return itemSolicitadoMapper.toResponse(item);
    }

    @Transactional
    public void remover(
            Integer idOferta,
            Integer idItemSolicitado
    ) {
        ItemSolicitado item = buscarEntidade(
                idOferta,
                idItemSolicitado
        );

        validarTrocaPendente(item.getTroca());

        itemSolicitadoRepository.delete(item);
    }

    private ItemSolicitado buscarEntidade(
            Integer idOferta,
            Integer idItemSolicitado
    ) {
        ItemSolicitado item = itemSolicitadoRepository
                .findById(idItemSolicitado)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Item solicitado não encontrado."
                        )
                );

        if (!item.getTroca().getIdOferta().equals(idOferta)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item solicitado não encontrado nessa troca."
            );
        }

        return item;
    }

    private Troca buscarTroca(Integer idOferta) {
        return trocaRepository
                .findById(idOferta)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Troca não encontrada."
                        )
                );
    }

    private void validarTrocaPendente(Troca troca) {
        if (!troca.getOferta().estaPendente()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Os itens de uma troca não pendente não podem ser alterados."
            );
        }
    }
}