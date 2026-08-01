package br.com.marketplace.service;

import br.com.marketplace.dto.itemOfertado.AtualizarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemOfertado.ItemOfertadoResponse;
import br.com.marketplace.entity.ItemOfertado;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.PosseFigurinha;
import br.com.marketplace.mapper.ItemOfertadoMapper;
import br.com.marketplace.repository.ItemOfertadoRepository;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.PosseFigurinhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemOfertadoService {

    private final ItemOfertadoRepository itemOfertadoRepository;
    private final OfertaRepository ofertaRepository;
    private final PosseFigurinhaRepository posseFigurinhaRepository;
    private final ItemOfertadoMapper itemOfertadoMapper;

    @Transactional
    public ItemOfertadoResponse criar(
            Integer idOferta,
            CriarItemOfertadoRequest request
    ) {
        Oferta oferta = buscarOferta(idOferta);

        validarOfertaPendente(oferta);

        PosseFigurinha posse = posseFigurinhaRepository
                .findById(request.idPosse())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Posse de figurinha não encontrada."
                        )
                );

        validarDonoDaPosse(oferta, posse);

        ItemOfertado item = itemOfertadoMapper.toEntity(
                request,
                oferta,
                posse
        );

        ItemOfertado salvo =
                itemOfertadoRepository.save(item);

        return itemOfertadoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ItemOfertadoResponse> listarPorOferta(
            Integer idOferta
    ) {
        buscarOferta(idOferta);

        return itemOfertadoRepository
                .buscarTodosPorOferta(idOferta)
                .stream()
                .map(itemOfertadoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemOfertadoResponse buscar(
            Integer idOferta,
            Integer idItem
    ) {
        return itemOfertadoMapper.toResponse(
                buscarEntidade(idOferta, idItem)
        );
    }

    @Transactional
    public ItemOfertadoResponse atualizar(
            Integer idOferta,
            Integer idItem,
            AtualizarItemOfertadoRequest request
    ) {
        ItemOfertado item =
                buscarEntidade(idOferta, idItem);

        validarOfertaPendente(item.getOferta());

        itemOfertadoMapper.updateEntity(item, request);

        return itemOfertadoMapper.toResponse(item);
    }

    @Transactional
    public void remover(
            Integer idOferta,
            Integer idItem
    ) {
        ItemOfertado item =
                buscarEntidade(idOferta, idItem);

        validarOfertaPendente(item.getOferta());

        itemOfertadoRepository.delete(item);
    }

    private ItemOfertado buscarEntidade(
            Integer idOferta,
            Integer idItem
    ) {

        return itemOfertadoRepository
                .findById(idItem)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Item ofertado não encontrado."
                        )
                );
    }

    private Oferta buscarOferta(Integer idOferta) {
        return ofertaRepository
                .findById(idOferta)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Oferta não encontrada."
                        )
                );
    }

    private void validarOfertaPendente(Oferta oferta) {
        if (!oferta.estaPendente()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Os itens de uma oferta não pendente não podem ser alterados."
            );
        }
    }

    private void validarDonoDaPosse(
            Oferta oferta,
            PosseFigurinha posse
    ) {
        String cpfProponente =
                oferta.getUsuarioProponente().getCpf();

        String cpfDonoDaPosse =
                posse.getUsuario().getCpf();

        if (!cpfProponente.equals(cpfDonoDaPosse)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A posse informada não pertence ao proponente da oferta."
            );
        }
    }
}