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

/**
 * Serviço responsável por gerenciar os itens que um usuário deseja receber (solicita) 
 * ao criar uma oferta de Troca. Garante que os pedidos sejam válidos, não duplicados 
 * na mesma oferta e que a oferta principal esteja em um estado que permita alterações.
 */
@Service
@RequiredArgsConstructor
public class ItemSolicitadoService {

    private final ItemSolicitadoRepository itemSolicitadoRepository;
    private final TrocaRepository trocaRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final ItemSolicitadoMapper itemSolicitadoMapper;

    /**
     * Adiciona uma figurinha do catálogo como uma exigência (item solicitado) em uma oferta de Troca.
     * Verifica se a troca ainda está pendente, impede que a mesma figurinha seja solicitada 
     * mais de uma vez na mesma troca, e valida se a figurinha existe no sistema.
     *
     * @param idOferta Identificador da oferta de troca.
     * @param request  Objeto contendo o código e tipo da figurinha desejada.
     * @return ItemSolicitadoResponse contendo os dados do item salvo.
     * @throws ResponseStatusException Se a troca ou a figurinha não existirem (404 NOT FOUND),
     *                                 se a troca não estiver pendente (409 CONFLICT), ou
     *                                 se a figurinha já tiver sido solicitada nesta mesma troca (409 CONFLICT).
     */
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

    /**
     * Retorna a lista de todas as figurinhas que estão sendo exigidas em uma oferta de troca específica.
     *
     * @param idOferta Identificador da oferta de troca.
     * @return Lista de ItemSolicitadoResponse detalhando cada figurinha exigida.
     * @throws ResponseStatusException Se a troca não existir (404 NOT FOUND).
     */
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

    /**
     * Busca os detalhes de um item solicitado específico dentro de uma oferta.
     *
     * @param idOferta         Identificador da oferta de troca.
     * @param idItemSolicitado Identificador único do item solicitado.
     * @return ItemSolicitadoResponse contendo as informações formatadas do item.
     * @throws ResponseStatusException Se o item não existir ou não pertencer à oferta informada (404 NOT FOUND).
     */
    @Transactional(readOnly = true)
    public ItemSolicitadoResponse buscar(
            Integer idOferta,
            Integer idItemSolicitado
    ) {
        return itemSolicitadoMapper.toResponse(
                buscarEntidade(idOferta, idItemSolicitado)
        );
    }

    /**
     * Atualiza os dados de um item solicitado (ex: quantidade, condição exigida), desde que a 
     * oferta de troca ainda esteja com o status PENDENTE.
     *
     * @param idOferta         Identificador da oferta de troca.
     * @param idItemSolicitado Identificador único do item a ser atualizado.
     * @param request          Objeto contendo os novos dados para o item solicitado.
     * @return ItemSolicitadoResponse com os dados atualizados.
     * @throws ResponseStatusException Se o item não for encontrado (404 NOT FOUND) ou se a troca não estiver pendente (409 CONFLICT).
     */
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

    /**
     * Remove fisicamente um item solicitado das exigências de uma oferta de troca,
     * contanto que a oferta ainda esteja pendente.
     *
     * @param idOferta         Identificador da oferta de troca.
     * @param idItemSolicitado Identificador único do item a ser removido.
     * @throws ResponseStatusException Se o item não for encontrado (404 NOT FOUND) ou se a troca não estiver pendente (409 CONFLICT).
     */
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

    /**
     * Método utilitário privado para buscar um ItemSolicitado.
     * Além de verificar se o item existe no banco de dados, aplica uma camada de segurança 
     * garantindo que o item efetivamente pertence à oferta (`idOferta`) informada na URL da requisição.
     *
     * @param idOferta         Identificador da oferta informado na rota.
     * @param idItemSolicitado Identificador da entidade no banco de dados.
     * @return A entidade ItemSolicitado recuperada.
     * @throws ResponseStatusException Se o item não for encontrado ou houver incompatibilidade de IDs (404 NOT FOUND).
     */
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

    /**
     * Método utilitário privado para buscar uma Troca no banco de dados.
     *
     * @param idOferta Identificador da troca (herdado da Oferta).
     * @return Entidade Troca encontrada.
     * @throws ResponseStatusException Se a troca não existir (404 NOT FOUND).
     */
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

    /**
     * Valida se a oferta associada à troca ainda possui o status PENDENTE.
     * Protege as negociações contra modificações caso já tenham sido concretizadas, expiradas ou canceladas.
     *
     * @param troca A entidade Troca a ser avaliada.
     * @throws ResponseStatusException Se a oferta subjacente não estiver PENDENTE (409 CONFLICT).
     */
    private void validarTrocaPendente(Troca troca) {
        if (!troca.getOferta().estaPendente()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Os itens de uma troca não pendente não podem ser alterados."
            );
        }
    }
}