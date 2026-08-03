package br.com.marketplace.service;

import br.com.marketplace.dto.figurinha.AtualizarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.CriarFigurinhaRequest;
import br.com.marketplace.dto.figurinha.FigurinhaResponse;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.id.FigurinhaId;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.FigurinhaMapper;
import br.com.marketplace.repository.FigurinhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelo gerenciamento do catálogo oficial de figurinhas do marketplace.
 * Lida com o cadastro, busca, atualização e remoção das figurinhas base do sistema, 
 * independentemente de quem as possui.
 */
@Service
@RequiredArgsConstructor
public class FigurinhaService {

    private final FigurinhaRepository figurinhaRepository;
    private final FigurinhaMapper figurinhaMapper;

    /**
     * Cadastra uma nova figurinha no catálogo oficial do sistema.
     * Valida se já existe uma figurinha com a mesma chave composta (código + tipo).
     *
     * @param request Objeto contendo os dados necessários para criar a figurinha (código, tipo, nome, etc.).
     * @return FigurinhaResponse contendo os dados da figurinha recém-cadastrada.
     * @throws RecursoJaExisteException Se uma figurinha com o mesmo código e tipo já existir no banco de dados.
     */
    @Transactional
    public FigurinhaResponse criar(
            CriarFigurinhaRequest request
    ) {
        FigurinhaId id = new FigurinhaId(
                request.codigo(),
                request.tipo()
        );

        if(figurinhaRepository.existsById(id)){
            throw new RecursoJaExisteException(
                    "Essa figurinha já está cadastrada."
            );
        }

        Figurinha figurinha = figurinhaMapper.toEntity(request);

        Figurinha salva = figurinhaRepository.save(figurinha);

        return figurinhaMapper.toResponse(salva);
    }

    /**
     * Busca uma figurinha específica no catálogo através de sua chave composta.
     *
     * @param codigo Código da figurinha (ex: "BRA10").
     * @param tipo   Tipo da figurinha (ex: NORMAL, EXTRA).
     * @return FigurinhaResponse com os dados detalhados da figurinha.
     * @throws RecursoNaoEncontradoException Se a figurinha não for encontrada no catálogo.
     */
    @Transactional(readOnly = true)
    public FigurinhaResponse buscar(
            String codigo,
            TipoFigurinha tipo
    ) {
        return figurinhaMapper.toResponse(
                buscarEntidade(codigo, tipo)
        );
    }

    /**
     * Retorna uma lista com todas as figurinhas cadastradas no catálogo oficial do sistema.
     *
     * @return Lista de FigurinhaResponse contendo todo o catálogo.
     */
    @Transactional(readOnly = true)
    public List<FigurinhaResponse> listarTodos() {
        return figurinhaRepository.findAll()
                .stream()
                .map(figurinhaMapper::toResponse)
                .toList();
    }

    /**
     * Busca figurinhas no catálogo filtrando pelo nome. 
     * O método utiliza uma busca parcial (contém) e ignora diferenças de maiúsculas/minúsculas (case-insensitive).
     *
     * @param nome Fragmento do nome do jogador ou seleção a ser pesquisado.
     * @return Lista de FigurinhaResponse que correspondem ao critério de busca.
     */
    @Transactional(readOnly = true)
    public List<FigurinhaResponse> buscarPorNome(
            String nome
    ) {
        return figurinhaRepository
                .findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(figurinhaMapper::toResponse)
                .toList();
    }

    /**
     * Atualiza as informações de uma figurinha já existente no catálogo.
     *
     * @param codigo  Código da figurinha a ser atualizada.
     * @param tipo    Tipo da figurinha a ser atualizada.
     * @param request Objeto contendo os novos dados para a figurinha.
     * @return FigurinhaResponse contendo os dados atualizados.
     * @throws RecursoNaoEncontradoException Se a figurinha não for encontrada para atualização.
     */
    @Transactional
    public FigurinhaResponse atualizar(
            String codigo,
            TipoFigurinha tipo,
            AtualizarFigurinhaRequest request
    ) {
        Figurinha figurinha =
                buscarEntidade(codigo, tipo);

        figurinhaMapper.updateEntity(
                figurinha,
                request
        );

        return figurinhaMapper.toResponse(figurinha);
    }

    /**
     * Remove fisicamente uma figurinha do catálogo oficial do sistema.
     *
     * @param codigo Código da figurinha a ser removida.
     * @param tipo   Tipo da figurinha a ser removida.
     * @throws RecursoNaoEncontradoException Se a figurinha não for encontrada para exclusão.
     */
    @Transactional
    public void remover(
            String codigo,
            TipoFigurinha tipo
    ) {
        Figurinha figurinha =
                buscarEntidade(codigo, tipo);

        figurinhaRepository.delete(figurinha);
    }

    /**
     * Método utilitário privado para centralizar a busca por uma entidade Figurinha
     * e padronizar o lançamento da exceção caso ela não exista.
     *
     * @param codigo Código da figurinha.
     * @param tipo   Tipo da figurinha.
     * @return Entidade Figurinha bruta recuperada do banco de dados.
     * @throws RecursoNaoEncontradoException Se a figurinha não for encontrada.
     */
    private Figurinha buscarEntidade(
            String codigo,
            TipoFigurinha tipo
    ) {
        FigurinhaId id =
                new FigurinhaId(codigo, tipo);

        return figurinhaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Figurinha não encontrada."
                        )
                );
    }
}