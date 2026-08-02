package br.com.marketplace.service;

import br.com.marketplace.dto.desejaFigurinha.CriarDesejaFigurinhaRequest;
import br.com.marketplace.dto.desejaFigurinha.DesejaFigurinhaResponse;
import br.com.marketplace.entity.DesejaFigurinha;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.id.DesejaFigurinhaId;
import br.com.marketplace.entity.id.FigurinhaId;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.DesejaFigurinhaMapper;
import br.com.marketplace.repository.DesejaFigurinhaRepository;
import br.com.marketplace.repository.FigurinhaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por gerenciar a lista de desejos (wishlist) dos usuários.
 * Controla o vínculo entre um usuário e as figurinhas do catálogo que ele tem 
 * interesse em adquirir no marketplace.
 */
@Service
@RequiredArgsConstructor
public class DesejaFigurinhaService {

    private final DesejaFigurinhaRepository desejoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final DesejaFigurinhaMapper desejoMapper;

    /**
     * Adiciona uma nova figurinha à lista de desejos de um usuário.
     * Valida a existência do usuário, a existência da figurinha no catálogo geral,
     * e garante que a figurinha não seja inserida em duplicidade na lista do mesmo usuário.
     *
     * @param cpfUsuario CPF do usuário que está adicionando o desejo.
     * @param request    Objeto contendo o código e o tipo da figurinha desejada.
     * @return DesejaFigurinhaResponse contendo os dados do desejo registrado.
     * @throws RecursoNaoEncontradoException Se o usuário ou a figurinha informada não existirem no sistema.
     * @throws RecursoJaExisteException      Se o usuário já possuir essa exata figurinha em sua lista de desejos.
     */
    @Transactional
    public DesejaFigurinhaResponse adicionar(
            String cpfUsuario,
            CriarDesejaFigurinhaRequest request
    ) {
        Usuario usuario = usuarioRepository
                .findById(cpfUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );

        FigurinhaId figurinhaId = new FigurinhaId(
                request.codigoFigurinha(),
                request.tipoFigurinha()
        );

        Figurinha figurinha = figurinhaRepository
                .findById(figurinhaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Figurinha não encontrada."
                        )
                );

        DesejaFigurinhaId desejoId =
                new DesejaFigurinhaId(
                        figurinhaId.getCodigo(),
                        figurinhaId.getTipo(),
                        cpfUsuario
                );

        if (desejoRepository.existsById(desejoId)) {
            throw new RecursoJaExisteException(
                    "O usuário já deseja essa figurinha."
            );
        }

        DesejaFigurinha desejo =
                desejoMapper.toEntity(
                        usuario,
                        figurinha
                );

        DesejaFigurinha salvo =
                desejoRepository.save(desejo);

        return desejoMapper.toResponse(salvo);
    }

    /**
     * Retorna a lista completa de todas as figurinhas que um usuário específico deseja.
     *
     * @param cpfUsuario CPF do usuário a ser consultado.
     * @return Lista de DesejaFigurinhaResponse contendo os itens desejados.
     * @throws RecursoNaoEncontradoException Se o CPF informado não pertencer a nenhum usuário cadastrado.
     */
    @Transactional(readOnly = true)
    public List<DesejaFigurinhaResponse> listarPorUsuario(
            String cpfUsuario
    ) {
        if (!usuarioRepository.existsById(cpfUsuario)) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        return desejoRepository
                .findByUsuarioCpf(cpfUsuario)
                .stream()
                .map(desejoMapper::toResponse)
                .toList();
    }

    /**
     * Busca um registro específico de desejo, combinando o usuário e as características da figurinha.
     *
     * @param cpfUsuario CPF do usuário que adicionou o desejo.
     * @param codigo     Código da figurinha (ex: "BRA10").
     * @param tipo       Tipo da figurinha (ex: NORMAL, BRILHANTE).
     * @return DesejaFigurinhaResponse com as informações daquele desejo específico.
     * @throws RecursoNaoEncontradoException Se o registro de desejo não for encontrado.
     */
    @Transactional(readOnly = true)
    public DesejaFigurinhaResponse buscar(
            String cpfUsuario,
            String codigo,
            br.com.marketplace.entity.enums.TipoFigurinha tipo
    ) {
        DesejaFigurinhaId id =
                criarId(cpfUsuario, codigo, tipo);

        DesejaFigurinha desejo =
                desejoRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Desejo não encontrado."
                                )
                        );

        return desejoMapper.toResponse(desejo);
    }

    /**
     * Remove uma figurinha da lista de desejos de um usuário.
     *
     * @param cpfUsuario CPF do usuário que está removendo o desejo.
     * @param codigo     Código da figurinha a ser removida da lista.
     * @param tipo       Tipo da figurinha a ser removida.
     * @throws RecursoNaoEncontradoException Se o registro de desejo não existir para ser removido.
     */
    @Transactional
    public void remover(
            String cpfUsuario,
            String codigo,
            br.com.marketplace.entity.enums.TipoFigurinha tipo
    ) {
        DesejaFigurinhaId id =
                criarId(cpfUsuario, codigo, tipo);

        DesejaFigurinha desejo =
                desejoRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Desejo não encontrado."
                                )
                        );

        desejoRepository.delete(desejo);
    }

    /**
     * Método utilitário interno responsável por montar a chave composta (DesejaFigurinhaId).
     * Como a entidade DesejaFigurinha depende da chave composta de Figurinha, 
     * este método simplifica a instanciação desses identificadores.
     *
     * @param cpfUsuario CPF do usuário dono da lista de desejos.
     * @param codigo     Código da figurinha desejada.
     * @param tipo       Tipo da figurinha desejada.
     * @return DesejaFigurinhaId instanciado e pronto para consultas no banco.
     */
    private DesejaFigurinhaId criarId(
            String cpfUsuario,
            String codigo,
            br.com.marketplace.entity.enums.TipoFigurinha tipo
    ) {
        FigurinhaId figurinhaId =
                new FigurinhaId(codigo, tipo);

        return new DesejaFigurinhaId(
                figurinhaId.getCodigo(),
                figurinhaId.getTipo(),
                cpfUsuario
        );
    }
}