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

/**
 * Serviço responsável por gerenciar os itens específicos (figurinhas) que estão sendo 
 * oferecidos em uma negociação. Garante que os itens adicionados a uma oferta 
 * realmente pertençam ao proponente e que a oferta esteja em um estado válido para alterações.
 */
@Service
@RequiredArgsConstructor
public class ItemOfertadoService {

    private final ItemOfertadoRepository itemOfertadoRepository;
    private final OfertaRepository ofertaRepository;
    private final PosseFigurinhaRepository posseFigurinhaRepository;
    private final ItemOfertadoMapper itemOfertadoMapper;

    /**
     * Adiciona uma figurinha do inventário do usuário (PosseFigurinha) como um item de uma oferta.
     * Realiza validações críticas: verifica se a oferta ainda está aberta (pendente) e 
     * garante que a figurinha informada realmente pertence ao usuário criador da oferta.
     *
     * @param idOferta Identificador da oferta à qual o item será vinculado.
     * @param request  Objeto contendo o ID da posse da figurinha a ser ofertada e outros detalhes.
     * @return ItemOfertadoResponse contendo os dados do item salvo.
     * @throws ResponseStatusException Se a oferta ou a posse não forem encontradas (404 NOT FOUND),
     *                                 se a oferta não estiver pendente (409 CONFLICT), ou 
     *                                 se a posse pertencer a outro usuário (400 BAD REQUEST).
     */
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

    /**
     * Retorna todos os itens (figurinhas) que compõem uma determinada oferta.
     *
     * @param idOferta Identificador da oferta.
     * @return Lista de ItemOfertadoResponse contendo os detalhes de cada figurinha oferecida.
     * @throws ResponseStatusException Se a oferta especificada não existir (404 NOT FOUND).
     */
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

    /**
     * Busca os detalhes de um item específico que foi ofertado, através do seu ID.
     *
     * @param idOferta Identificador da oferta (usado apenas na assinatura do controller/rota atual, a busca é pelo idItem).
     * @param idItem   Identificador único do item ofertado.
     * @return ItemOfertadoResponse com as informações do item.
     * @throws ResponseStatusException Se o item não for encontrado (404 NOT FOUND).
     */
    @Transactional(readOnly = true)
    public ItemOfertadoResponse buscar(
            Integer idOferta,
            Integer idItem
    ) {
        return itemOfertadoMapper.toResponse(
                buscarEntidade(idOferta, idItem)
        );
    }

    /**
     * Atualiza as informações de um item que já está dentro de uma oferta.
     * Só permite a atualização se a oferta principal ainda estiver com o status PENDENTE.
     *
     * @param idOferta Identificador da oferta associada ao item.
     * @param idItem   Identificador único do item a ser atualizado.
     * @param request  Objeto contendo os novos dados do item ofertado.
     * @return ItemOfertadoResponse com os dados atualizados.
     * @throws ResponseStatusException Se o item não for encontrado (404 NOT FOUND) ou se a oferta não estiver pendente (409 CONFLICT).
     */
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

    /**
     * Remove fisicamente um item ofertado de uma oferta existente.
     * Só permite a remoção se a oferta principal ainda estiver com o status PENDENTE.
     *
     * @param idOferta Identificador da oferta associada ao item.
     * @param idItem   Identificador único do item a ser removido.
     * @throws ResponseStatusException Se o item não for encontrado (404 NOT FOUND) ou se a oferta não estiver pendente (409 CONFLICT).
     */
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

    /**
     * Método utilitário privado para buscar um ItemOfertado no banco de dados
     * e padronizar o lançamento do erro HTTP apropriado caso não exista.
     *
     * @param idOferta Identificador da oferta.
     * @param idItem   Identificador do item.
     * @return A entidade ItemOfertado localizada.
     * @throws ResponseStatusException Se não for localizada (404 NOT FOUND).
     */
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

    /**
     * Método utilitário privado para buscar uma Oferta no banco de dados.
     *
     * @param idOferta Identificador da oferta.
     * @return A entidade Oferta localizada.
     * @throws ResponseStatusException Se não for localizada (404 NOT FOUND).
     */
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

    /**
     * Valida se a oferta possui o status PENDENTE. 
     * Impede modificações no conteúdo de ofertas que já foram concretizadas, canceladas ou expiradas.
     *
     * @param oferta A entidade Oferta a ser validada.
     * @throws ResponseStatusException Se o status for diferente de PENDENTE (409 CONFLICT).
     */
    private void validarOfertaPendente(Oferta oferta) {
        if (!oferta.estaPendente()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Os itens de uma oferta não pendente não podem ser alterados."
            );
        }
    }

    /**
     * Validação de segurança que garante que a posse da figurinha que está sendo ofertada
     * pertence ao mesmo usuário que é o criador (proponente) da oferta.
     * Evita que um usuário oferte figurinhas do inventário de terceiros.
     *
     * @param oferta A oferta sendo montada.
     * @param posse  A figurinha do inventário do usuário.
     * @throws ResponseStatusException Se os CPFs do dono da posse e do proponente não coincidirem (400 BAD REQUEST).
     */
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