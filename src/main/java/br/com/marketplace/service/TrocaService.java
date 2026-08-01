package br.com.marketplace.service;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.itemSolicitado.CriarItemSolicitadoRequest;
import br.com.marketplace.dto.troca.AtualizarTrocaRequest;
import br.com.marketplace.dto.troca.CriarTrocaRequest;
import br.com.marketplace.dto.troca.TrocaResponse;
import br.com.marketplace.entity.*;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.entity.id.FigurinhaId;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.exception.RegraDeNegocioException;
import br.com.marketplace.mapper.TrocaMapper;
import br.com.marketplace.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrocaService {

    private final TrocaRepository trocaRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PosseFigurinhaRepository posseFigurinhaRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final TrocaMapper trocaMapper;

    @Transactional
    public TrocaResponse criar(
            String cpfProponente,
            CriarTrocaRequest request
    ) {
        Usuario proponente = buscarUsuario(cpfProponente);

        Oferta oferta = new Oferta(
                TipoOferta.TROCA,
                proponente,
                request.prazoLimite(),
                request.descricao()
        );

        adicionarItensOfertados(
                oferta,
                proponente,
                request.itensOfertados()
        );

        Troca troca = trocaMapper.toEntity(
                request,
                oferta
        );

        adicionarItensSolicitados(
                troca,
                request.itensSolicitados()
        );

        oferta.calcularValorDeMercado();

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        return trocaMapper.toResponse(
                ofertaSalva.getTroca()
        );
    }

    @Transactional
    public TrocaResponse atualizar(
            Integer idOferta,
            AtualizarTrocaRequest request
    ) {
        Troca troca = buscarEntidade(idOferta);

        troca.getOferta().atualizarOferta(
                request.prazoLimite(),
                request.descricao()
        );

        return trocaMapper.toResponse(troca);
    }

    @Transactional(readOnly = true)
    public TrocaResponse buscar(Integer idOferta) {
        return trocaMapper.toResponse(
                buscarEntidade(idOferta)
        );
    }

    @Transactional(readOnly = true)
    public List<TrocaResponse> listarTodas() {
        return trocaRepository.findAll()
                .stream()
                .map(trocaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrocaResponse> listarPorProponente(
            String cpf
    ) {
        return trocaRepository
                .findByOfertaUsuarioProponenteCpf(cpf)
                .stream()
                .map(trocaMapper::toResponse)
                .toList();
    }

    @Transactional
    public void remover(Integer idOferta) {
        Troca troca = buscarEntidade(idOferta);

        if (!troca.getOferta().estaPendente()) {
            throw new RegraDeNegocioException(
                    "Apenas trocas pendentes podem ser removidas."
            );
        }

        ofertaRepository.delete(
                troca.getOferta()
        );
    }

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

    private void adicionarItensSolicitados(
            Troca troca,
            List<CriarItemSolicitadoRequest> itens
    ) {
        for (CriarItemSolicitadoRequest itemRequest : itens) {

            FigurinhaId figurinhaId = new FigurinhaId(
                    itemRequest.codigoFigurinha(),
                    itemRequest.tipoFigurinha()
            );

            Figurinha figurinha = figurinhaRepository
                    .findById(figurinhaId)
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Figurinha "
                                            + itemRequest.codigoFigurinha()
                                            + " do tipo "
                                            + itemRequest.tipoFigurinha()
                                            + " não encontrada."
                            )
                    );

            ItemSolicitado item = new ItemSolicitado(
                    troca,
                    figurinha,
                    itemRequest.quantidade()
            );

            troca.adicionarItemSolicitado(item);
        }
    }

    private void validarPosseDoProponente(
            PosseFigurinha posse,
            Usuario proponente
    ) {
        if (!posse.getUsuario()
                .getCpf()
                .equals(proponente.getCpf())) {

            throw new RegraDeNegocioException(
                    "A posse informada não pertence ao proponente da troca."
            );
        }
    }

    private Usuario buscarUsuario(String cpf) {
        return usuarioRepository.findById(cpf)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }

    private Troca buscarEntidade(Integer idOferta) {
        return trocaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Troca não encontrada."
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
}