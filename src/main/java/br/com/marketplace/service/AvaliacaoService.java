package br.com.marketplace.service;

import br.com.marketplace.dto.avaliacao.AtualizarAvaliacaoRequest;
import br.com.marketplace.dto.avaliacao.AvaliacaoResponse;
import br.com.marketplace.dto.avaliacao.CriarAvaliacaoRequest;
import br.com.marketplace.entity.Avaliacao;
import br.com.marketplace.entity.Concretizacao;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.id.AvaliacaoId;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.exception.RegraDeNegocioException;
import br.com.marketplace.mapper.AvaliacaoMapper;
import br.com.marketplace.repository.AvaliacaoRepository;
import br.com.marketplace.repository.ConcretizacaoRepository;
import br.com.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ConcretizacaoRepository concretizacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoMapper avaliacaoMapper;

    @Transactional
    public AvaliacaoResponse criar(
            Integer idConcretizacao,
            CriarAvaliacaoRequest request
    ) {
        Concretizacao concretizacao =
                concretizacaoRepository
                        .findById(idConcretizacao)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Concretização não encontrada."
                                )
                        );

        Usuario avaliador = usuarioRepository
                .findById(request.cpfAvaliador())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário avaliador não encontrado."
                        )
                );

        Usuario avaliado = concretizacao
                .getOferta()
                .getUsuarioProponente();

        validarParticipantes(
                concretizacao,
                avaliador,
                avaliado
        );

        AvaliacaoId id = new AvaliacaoId(
                avaliador.getCpf(),
                avaliado.getCpf(),
                idConcretizacao
        );

        if (avaliacaoRepository.existsById(id)) {
            throw new RecursoJaExisteException(
                    "Essa avaliação já foi registrada."
            );
        }

        Avaliacao avaliacao =
                avaliacaoMapper.toEntity(
                        request,
                        concretizacao,
                        avaliador,
                        avaliado
                );

        Avaliacao salva =
                avaliacaoRepository.saveAndFlush(avaliacao);

        atualizarMediaAvaliado(avaliado);

        return avaliacaoMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponse buscar(
            String cpfAvaliador,
            String cpfAvaliado,
            Integer idConcretizacao
    ) {
        AvaliacaoId id = new AvaliacaoId(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao
        );

        return avaliacaoMapper.toResponse(
                buscarEntidade(id)
        );
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listarPorAvaliado(
            String cpf
    ) {
        return avaliacaoRepository
                .findByUsuarioAvaliadoCpf(cpf)
                .stream()
                .map(avaliacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listarPorAvaliador(
            String cpf
    ) {
        return avaliacaoRepository
                .findByUsuarioAvaliadorCpf(cpf)
                .stream()
                .map(avaliacaoMapper::toResponse)
                .toList();
    }

    @Transactional
    public AvaliacaoResponse atualizar(
            String cpfAvaliador,
            String cpfAvaliado,
            Integer idConcretizacao,
            AtualizarAvaliacaoRequest request
    ) {
        AvaliacaoId id = new AvaliacaoId(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao
        );

        Avaliacao avaliacao =
                buscarEntidade(id);

        avaliacao.alterarAvaliacao(
                request.nota(),
                request.comentario()
        );

        avaliacaoRepository.flush();

        atualizarMediaAvaliado(
                avaliacao.getUsuarioAvaliado()
        );

        return avaliacaoMapper.toResponse(avaliacao);
    }

    @Transactional
    public void remover(
            String cpfAvaliador,
            String cpfAvaliado,
            Integer idConcretizacao
    ) {
        AvaliacaoId id = new AvaliacaoId(
                cpfAvaliador,
                cpfAvaliado,
                idConcretizacao
        );

        Avaliacao avaliacao =
                buscarEntidade(id);

        Usuario avaliado =
                avaliacao.getUsuarioAvaliado();

        avaliacaoRepository.delete(avaliacao);
        avaliacaoRepository.flush();

        atualizarMediaAvaliado(avaliado);
    }

    private void validarParticipantes(
            Concretizacao concretizacao,
            Usuario avaliador,
            Usuario avaliado
    ) {
        if (!concretizacao.getAceitante()
                .getCpf()
                .equals(avaliador.getCpf())) {
            throw new RegraDeNegocioException(
                    "Apenas o aceitante pode avaliar essa concretização."
            );
        }

        if (avaliador.getCpf()
                .equals(avaliado.getCpf())) {
            throw new RegraDeNegocioException(
                    "Um usuário não pode avaliar a si mesmo."
            );
        }
    }

    private void atualizarMediaAvaliado(
            Usuario usuarioAvaliado
    ) {
        BigDecimal media =
                avaliacaoRepository
                        .calcularMediaPorAvaliado(
                                usuarioAvaliado.getCpf()
                        );

        usuarioAvaliado.atualizarAvaliacaoMedia(
                media
        );
    }

    private Avaliacao buscarEntidade(
            AvaliacaoId id
    ) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Avaliação não encontrada."
                        )
                );
    }
}