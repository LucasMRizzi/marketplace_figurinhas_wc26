package br.com.marketplace.service;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.venda.AtualizarVendaRequest;
import br.com.marketplace.dto.venda.CriarVendaRequest;
import br.com.marketplace.dto.venda.VendaResponse;
import br.com.marketplace.entity.*;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.exception.RegraDeNegocioException;
import br.com.marketplace.mapper.VendaMapper;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.PosseFigurinhaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import br.com.marketplace.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por gerenciar as negociações do tipo Venda no marketplace.
 * Trabalha em conjunto com a entidade base Oferta, garantindo que toda negociação 
 * de venda possua uma Oferta matriz associada para controle de status e histórico.
 */
@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PosseFigurinhaRepository posseFigurinhaRepository;
    private final VendaMapper vendaMapper;

    /**
     * Cria uma nova proposta de venda no sistema.
     * O processo ocorre em duas etapas: primeiro, a entidade base (Oferta) é criada e persistida. 
     * Em seguida, a entidade específica (Venda) é instanciada vinculada à oferta e salva no banco.
     *
     * @param cpfProponente CPF do usuário que está propondo a venda.
     * @param request       Objeto contendo os dados específicos da venda (prazo limite, descrição, preço unitário e quantidade).
     * @return VendaResponse com os dados da venda recém-criada.
     * @throws RecursoNaoEncontradoException Se o usuário proponente não existir no banco de dados.
     */
    @Transactional
    public VendaResponse criar(
            String cpfProponente,
            CriarVendaRequest request
    ) {
        Usuario proponente = buscarUsuario(cpfProponente);

        Oferta oferta = new Oferta(
                TipoOferta.VENDA,
                proponente,
                request.prazoLimite(),
                request.descricao()
        );

        adicionarItensOfertados(
                oferta,
                proponente,
                request.itensOfertados()
        );

        oferta.calcularValorDeMercado();

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        Venda venda = new Venda(
                ofertaSalva,
                request.valorDaProposta()
        );

        Venda vendaSalva = vendaRepository.save(venda);

        return vendaMapper.toResponse(vendaSalva);
    }

    /**
     * Busca os detalhes de uma proposta de venda específica.
     *
     * @param idOferta Identificador único da oferta matriz vinculada à venda.
     * @return VendaResponse contendo os dados formatados.
     * @throws RecursoNaoEncontradoException Se não existir uma venda associada ao ID informado.
     */
    @Transactional(readOnly = true)
    public VendaResponse buscar (Integer idOferta){
        return vendaMapper.toResponse(
                buscarEntidade(idOferta)
        );
    }

    /**
     * Retorna um histórico com todas as propostas de venda registradas no marketplace.
     *
     * @return Lista contendo todas as VendaResponse.
     */
    @Transactional(readOnly = true)
    public List<VendaResponse> listarTodas () {
        return vendaRepository.findAll()
                .stream()
                .map(vendaMapper::toResponse)
                .toList();
    }

    /**
     * Lista todas as propostas de venda ativas ou inativas criadas por um usuário específico.
     *
     * @param cpf CPF do usuário proponente (vendedor).
     * @return Lista de VendaResponse vinculadas a este usuário.
     */
    @Transactional(readOnly = true)
    public List<VendaResponse> listarPorProponente (
            String cpf
    ){
        return vendaRepository
                .findByOfertaUsuarioProponenteCpf(cpf)
                .stream()
                .map(vendaMapper::toResponse)
                .toList();
    }

    /**
     * Atualiza os dados financeiros e quantitativos de uma venda em andamento.
     *
     * @param idOferta Identificador único da oferta vinculada à venda.
     * @param request  Objeto contendo os novos valores (preço unitário e quantidade).
     * @return VendaResponse com os dados atualizados da venda.
     * @throws RecursoNaoEncontradoException Se a venda não for encontrada para atualização.
     */
    @Transactional
    public VendaResponse atualizar (
            Integer idOferta,
            AtualizarVendaRequest request
    ){
        Venda venda = buscarEntidade(idOferta);
        Oferta oferta = buscarOferta(idOferta);

        venda.atualizarVenda(
                request.valorDaProposta()
        );

        oferta.atualizarOferta(
                request.prazoLimite(),
                request.descricao()
        );

        return vendaMapper.toResponse(venda);
    }

    /**
     * Remove uma proposta de venda do sistema.
     * A exclusão é realizada deletando a entidade matriz (Oferta). Por conta das 
     * configurações de mapeamento (cascade), a entidade Venda associada também é deletada.
     *
     * @param idOferta Identificador da oferta vinculada à venda que será deletada.
     * @throws RecursoNaoEncontradoException Se a venda não for localizada.
     */
    @Transactional
    public void remover (Integer idOferta){
        Venda venda = buscarEntidade(idOferta);

        ofertaRepository.delete(
                venda.getOferta()
        );
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

    /**
     * TODO
     */
    private void adicionarItensOfertados(
            Oferta oferta,
            Usuario proponente,
            List<CriarItemOfertadoRequest> itens
    ) {
        for (CriarItemOfertadoRequest itemRequest : itens) {

            PosseFigurinha posse = posseFigurinhaRepository
                    .findById(itemRequest.idPosse())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Posse de ID "
                                            + itemRequest.idPosse()
                                            + " não encontrada."
                            )
                    );

            validarPosseDoProponente(
                    posse,
                    proponente
            );

            ItemOfertado item = new ItemOfertado(
                    oferta,
                    posse,
                    itemRequest.quantidadeOfertada(),
                    itemRequest.condicao(),
                    itemRequest.foto()
            );

            oferta.adicionarItemOfertado(item);
        }
    }

    /**
     * =========================================================
     * Buscas Auxiliares
     * =========================================================
     */

    private Venda buscarEntidade(Integer idOferta) {
        return vendaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Venda não encontrada."
                        )
                );
    }

    private Usuario buscarUsuario(String cpf) {
        return usuarioRepository.findById(cpf)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }

    private Oferta buscarOferta (Integer idOferta){
        return ofertaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Oferta não encontrada."
                        )
                );
    }

    /**
     * =========================================================
     * Validações Auxiliares
     * =========================================================
     */

    private void validarPosseDoProponente(
            PosseFigurinha posse,
            Usuario proponente
    ) {
        if (!posse.getUsuario()
                .getCpf()
                .equals(proponente.getCpf())) {

            throw new RegraDeNegocioException(
                    "A posse informada não pertence ao proponente da oferta."
            );
        }
    }
}