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

/**
 * Serviço responsável pelo gerenciamento de avaliações (feedbacks) entre usuários.
 * Atua no processamento das notas após a conclusão de uma negociação (Concretização),
 * garantindo as regras de negócio e mantendo a nota média dos usuários sempre atualizada.
 */
@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ConcretizacaoRepository concretizacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoMapper avaliacaoMapper;

    /**
     * Registra uma nova avaliação referente a uma negociação concretizada.
     * Após salvar a avaliação, recalcula automaticamente a nota média do usuário avaliado.
     *
     * @param idConcretizacao Identificador da negociação finalizada.
     * @param request         Objeto contendo a nota, comentário e o CPF do avaliador.
     * @return AvaliacaoResponse contendo os dados da avaliação recém-criada.
     * @throws RecursoNaoEncontradoException Se a concretização ou o usuário avaliador não existirem.
     * @throws RegraDeNegocioException       Se o avaliador tentar avaliar a si mesmo, ou se não for o aceitante da oferta.
     * @throws RecursoJaExisteException      Se já existir uma avaliação registrada para esta mesma negociação entre os mesmos usuários.
     */
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

    /**
     * Recupera os dados de uma avaliação específica utilizando sua chave composta.
     *
     * @param cpfAvaliador    CPF do usuário que realizou a avaliação.
     * @param cpfAvaliado     CPF do usuário que recebeu a avaliação.
     * @param idConcretizacao ID da negociação avaliada.
     * @return AvaliacaoResponse com as informações formatadas.
     * @throws RecursoNaoEncontradoException Se a avaliação não existir no banco.
     */
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

    /**
     * Retorna a lista de todas as avaliações que um determinado usuário recebeu.
     *
     * @param cpf CPF do usuário avaliado.
     * @return Lista de AvaliacaoResponse recebidas pelo usuário.
     */
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

    /**
     * Retorna a lista de todas as avaliações que um determinado usuário fez sobre outros.
     *
     * @param cpf CPF do usuário avaliador.
     * @return Lista de AvaliacaoResponse feitas pelo usuário.
     */
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

    /**
     * Modifica a nota e/ou comentário de uma avaliação já existente.
     * O método garante que a nota média do usuário avaliado seja recalculada após a alteração.
     *
     * @param cpfAvaliador    CPF do usuário que realizou a avaliação original.
     * @param cpfAvaliado     CPF do usuário que recebeu a avaliação.
     * @param idConcretizacao ID da negociação vinculada à avaliação.
     * @param request         Objeto contendo os novos dados (nota e/ou comentário).
     * @return AvaliacaoResponse contendo a avaliação atualizada.
     * @throws RecursoNaoEncontradoException Se a avaliação original não for encontrada.
     */
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

    /**
     * Exclui uma avaliação do banco de dados e recalcula a nota média do usuário que havia sido avaliado.
     *
     * @param cpfAvaliador    CPF do usuário que realizou a avaliação.
     * @param cpfAvaliado     CPF do usuário que recebeu a avaliação.
     * @param idConcretizacao ID da negociação vinculada à avaliação.
     * @throws RecursoNaoEncontradoException Se a avaliação não for encontrada para exclusão.
     */
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

    /**
     * Valida as regras de negócio referentes à elegibilidade para realizar uma avaliação.
     * Garante que apenas a pessoa que aceitou a oferta possa avaliar o dono da oferta, 
     * e impede autoavaliações.
     *
     * @param concretizacao A negociação concretizada.
     * @param avaliador     O usuário tentando realizar a avaliação.
     * @param avaliado      O usuário que está recebendo a avaliação.
     * @throws RegraDeNegocioException Se alguma regra for violada.
     */
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

    /**
     * Recalcula a nota média geral de um usuário com base em todas as avaliações 
     * registradas no banco de dados e atualiza a entidade Usuario.
     *
     * @param usuarioAvaliado O usuário que terá sua média atualizada.
     */
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

    /**
     * Método utilitário para centralizar a busca por uma entidade Avaliacao e 
     * padronizar o lançamento da exceção de não encontrada.
     *
     * @param id Chave composta (AvaliacaoId) da avaliação.
     * @return Entidade Avaliacao encontrada.
     * @throws RecursoNaoEncontradoException Se a avaliação não for encontrada.
     */
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