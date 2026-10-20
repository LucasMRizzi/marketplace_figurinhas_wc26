package br.com.marketplace.service;

import br.com.marketplace.dto.itemOfertado.CriarItemOfertadoRequest;
import br.com.marketplace.dto.propostaTroca.CriarPropostaTrocaRequest;
import br.com.marketplace.dto.propostaTroca.PropostaTrocaResponse;
import br.com.marketplace.entity.*;
import br.com.marketplace.entity.enums.StatusPropostaTroca;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.exception.RegraDeNegocioException;
import br.com.marketplace.mapper.PropostaTrocaMapper;
import br.com.marketplace.repository.ConcretizacaoRepository;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.PosseFigurinhaRepository;
import br.com.marketplace.repository.PropostaTrocaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropostaTrocaService {
    private final PropostaTrocaRepository propostaRepository;
    private final OfertaRepository ofertaRepository;
    private final PosseFigurinhaRepository posseRepository;
    private final ConcretizacaoRepository concretizacaoRepository;
    private final PropostaTrocaMapper mapper;

    @Transactional
    public PropostaTrocaResponse criar(Integer idOferta, Usuario solicitante,
                                        CriarPropostaTrocaRequest request) {
        Oferta oferta = buscarOferta(idOferta);
        if (!oferta.ehTroca() || !oferta.estaPendente()) {
            throw new RegraDeNegocioException("Esta oferta não está disponível para propostas de troca.");
        }
        if (propostaRepository.existsByOfertaIdOfertaAndSolicitanteCpfAndStatus(
                idOferta, solicitante.getCpf(), StatusPropostaTroca.PENDENTE)) {
            throw new RecursoJaExisteException("Você já possui uma proposta pendente para esta troca.");
        }

        PropostaTroca proposta = new PropostaTroca(oferta, solicitante);
        for (CriarItemOfertadoRequest itemRequest : request.itens()) {
            PosseFigurinha posse = posseRepository.findById(itemRequest.idPosse())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Posse de ID " + itemRequest.idPosse() + " não encontrada."));
            validarPosse(posse, solicitante, itemRequest.quantidadeOfertada());
            proposta.adicionarItem(new ItemPropostaTroca(
                    proposta, posse, itemRequest.quantidadeOfertada(),
                    itemRequest.condicao(), itemRequest.foto()));
        }
        return mapper.toResponse(propostaRepository.save(proposta));
    }

    @Transactional(readOnly = true)
    public List<PropostaTrocaResponse> listarRecebidas(String cpfProponente) {
        return propostaRepository.findByOfertaUsuarioProponenteCpfAndStatusOrderByDataCriacaoDesc(
                        cpfProponente, StatusPropostaTroca.PENDENTE)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PropostaTrocaResponse> listarEnviadas(String cpfSolicitante) {
        return propostaRepository.findBySolicitanteCpfAndStatusOrderByDataCriacaoDesc(
                        cpfSolicitante, StatusPropostaTroca.PENDENTE)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PropostaTrocaResponse buscar(Integer idProposta, Usuario usuario) {
        PropostaTroca proposta = buscarProposta(idProposta);
        boolean participante = proposta.getSolicitante().getCpf().equals(usuario.getCpf())
                || proposta.getOferta().getUsuarioProponente().getCpf().equals(usuario.getCpf());
        if (!participante) {
            throw new RegraDeNegocioException("Você não participa desta proposta de troca.");
        }
        return mapper.toResponse(proposta);
    }

    @Transactional
    public PropostaTrocaResponse aceitar(Integer idProposta, Usuario proponente) {
        PropostaTroca proposta = buscarProposta(idProposta);
        ofertaRepository.buscarParaAtualizacao(proposta.getOferta().getIdOferta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oferta não encontrada."));
        validarProponente(proposta, proponente);
        if (!proposta.getOferta().estaPendente()) {
            throw new RegraDeNegocioException("A oferta não está mais disponível.");
        }
        if (concretizacaoRepository.existsByOfertaIdOferta(proposta.getOferta().getIdOferta())) {
            throw new RecursoJaExisteException("Esta oferta já foi concretizada.");
        }
        proposta.getItens().forEach(item ->
                validarPosse(item.getPosse(), proposta.getSolicitante(), item.getQuantidade()));

        Concretizacao concretizacao = new Concretizacao(proposta.getOferta(), proposta.getSolicitante());
        concretizacao.confirmarTroca("TROCA-" + idProposta);
        concretizacaoRepository.saveAndFlush(concretizacao);
        proposta.aceitar();
        proposta.getOferta().concretizar();

        propostaRepository.findByOfertaIdOfertaAndStatus(
                        proposta.getOferta().getIdOferta(), StatusPropostaTroca.PENDENTE)
                .stream().filter(outra -> !outra.getIdProposta().equals(idProposta))
                .forEach(PropostaTroca::recusar);
        return mapper.toResponse(proposta);
    }

    @Transactional
    public PropostaTrocaResponse recusar(Integer idProposta, Usuario proponente) {
        PropostaTroca proposta = buscarProposta(idProposta);
        validarProponente(proposta, proponente);
        proposta.recusar();
        return mapper.toResponse(proposta);
    }

    private void validarProponente(PropostaTroca proposta, Usuario usuario) {
        if (!proposta.getOferta().getUsuarioProponente().getCpf().equals(usuario.getCpf())) {
            throw new RegraDeNegocioException("Somente o dono da oferta pode responder esta proposta.");
        }
    }

    private void validarPosse(PosseFigurinha posse, Usuario usuario, Integer quantidade) {
        if (!posse.getUsuario().getCpf().equals(usuario.getCpf())) {
            throw new RegraDeNegocioException("Uma das figurinhas não pertence ao solicitante.");
        }
        if (quantidade == null || quantidade <= 0 || quantidade > posse.getQuantidade()) {
            throw new RegraDeNegocioException("Quantidade indisponível para " + posse.getNomeFigurinha() + ".");
        }
    }

    private Oferta buscarOferta(Integer idOferta) {
        return ofertaRepository.findById(idOferta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oferta não encontrada."));
    }

    private PropostaTroca buscarProposta(Integer idProposta) {
        return propostaRepository.findById(idProposta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Proposta de troca não encontrada."));
    }
}
