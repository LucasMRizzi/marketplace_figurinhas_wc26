package br.com.marketplace.service;

import br.com.marketplace.dto.posseFigurinha.CriarPosseFigurinhaRequest;
import br.com.marketplace.dto.posseFigurinha.PosseFigurinhaResponse;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.PosseFigurinha;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.id.FigurinhaId;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.PosseFigurinhaMapper;
import br.com.marketplace.repository.FigurinhaRepository;
import br.com.marketplace.repository.PosseFigurinhaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelo gerenciamento do inventário pessoal (posses) de figurinhas dos usuários.
 * Controla quais figurinhas cada usuário tem guardadas para uso (seja para colar no álbum ou para 
 * oferecer em negociações), lidando de forma inteligente com o incremento e decremento de quantidades.
 */
@Service
@RequiredArgsConstructor
public class PosseFigurinhaService {

    private final PosseFigurinhaRepository posseRepository;
    private final UsuarioRepository usuarioRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final PosseFigurinhaMapper posseMapper;

    /**
     * Adiciona uma ou mais cópias de uma figurinha ao inventário do usuário.
     * O método verifica se o usuário já possui um registro para esta figurinha:
     * se não possuir, cria um novo registro; se já possuir, apenas atualiza (incrementa) a quantidade.
     *
     * @param cpfUsuario CPF do usuário dono do inventário.
     * @param request    Objeto contendo o código, tipo da figurinha e a quantidade adquirida.
     * @return PosseFigurinhaResponse com os dados atualizados do registro de posse.
     * @throws RecursoNaoEncontradoException Se o usuário ou a figurinha do catálogo não existirem.
     */
    @Transactional
    public PosseFigurinhaResponse adicionar(
            String cpfUsuario,
            CriarPosseFigurinhaRequest request
    ) {
        Usuario usuario = buscarUsuario(cpfUsuario);

        FigurinhaId figurinhaId = new FigurinhaId(
                request.codigoFigurinha(),
                request.tipoFigurinha()
        );

        Figurinha figurinha = buscarFigurinha(figurinhaId);

        PosseFigurinha posse = buscarPosse(cpfUsuario, figurinhaId);

        if (posse == null) {
            posse = posseMapper.toEntity(
                    usuario,
                    figurinha,
                    request
            );

            posse = posseRepository.save(posse);
        } else {
            posse.adicionarQuantidade(
                    request.quantidade()
            );
        }

        return posseMapper.toResponse(posse);
    }

    /**
     * Busca os detalhes de um registro de posse específico pelo seu identificador.
     *
     * @param idPosse Identificador único do registro de posse no banco de dados.
     * @return PosseFigurinhaResponse contendo os detalhes (usuário, figurinha, quantidade).
     * @throws RecursoNaoEncontradoException Se o registro não for localizado.
     */
    @Transactional(readOnly = true)
    public PosseFigurinhaResponse buscar(Integer idPosse) {
        return posseMapper.toResponse(
                buscarEntidade(idPosse)
        );
    }

    /**
     * Retorna a lista completa do inventário de figurinhas pertencente a um usuário específico.
     *
     * @param cpfUsuario CPF do usuário a ser consultado.
     * @return Lista de PosseFigurinhaResponse, representando tudo o que o usuário tem guardado.
     */
    @Transactional(readOnly = true)
    public List<PosseFigurinhaResponse> listarPorUsuario(
            String cpfUsuario
    ) {
        return posseRepository
                .findByUsuarioCpf(cpfUsuario)
                .stream()
                .map(posseMapper::toResponse)
                .toList();
    }

    /**
     * Deduz uma quantidade específica de figurinhas de um registro de posse.
     * Caso a subtração faça a quantidade chegar a zero, o registro da posse é 
     * automaticamente excluído do banco de dados para não deixar "estoques fantasmas".
     *
     * @param idPosse    Identificador do registro de posse que sofrerá a dedução.
     * @param quantidade Número de unidades a serem removidas.
     * @return PosseFigurinhaResponse com os dados atualizados (ou os últimos dados conhecidos antes da exclusão, se zerar).
     * @throws RecursoNaoEncontradoException Se o registro de posse não for encontrado.
     */
    @Transactional
    public PosseFigurinhaResponse removerQuantidade(
            Integer idPosse,
            int quantidade
    ) {
        PosseFigurinha posse =
                buscarEntidade(idPosse);

        posse.removerQuantidade(quantidade);

        if (posse.getQuantidade() == 0) {
            PosseFigurinhaResponse response =
                    posseMapper.toResponse(posse);

            posseRepository.delete(posse);

            return response;
        }

        return posseMapper.toResponse(posse);
    }

    /**
     * Exclui fisicamente um registro de posse de figurinha de forma forçada e completa, 
     * ignorando quantas unidades ainda restavam naquele registro.
     *
     * @param idPosse Identificador do registro de posse a ser deletado.
     * @throws RecursoNaoEncontradoException Se o registro de posse não existir.
     */
    @Transactional
    public void remover(Integer idPosse) {
        PosseFigurinha posse =
                buscarEntidade(idPosse);

        posseRepository.delete(posse);
    }

    /**
     * =========================================================
     * Buscas Auxiliares
     * =========================================================
     */

    /**
     * Método utilitário privado para buscar a entidade PosseFigurinha 
     * e centralizar o tratamento de erro de recurso não encontrado.
     *
     * @param idPosse Identificador da posse.
     * @return A entidade PosseFigurinha persistida no banco.
     * @throws RecursoNaoEncontradoException Se o ID informado não existir no banco.
     */
    private PosseFigurinha buscarEntidade(Integer idPosse) {
        return posseRepository.findById(idPosse)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Posse não encontrada."
                        )
                );
    }

    private Usuario buscarUsuario(String cpf){
        return usuarioRepository
                .findById(cpf)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }

    private Figurinha buscarFigurinha(FigurinhaId figurinhaId){
        return figurinhaRepository
                .findById(figurinhaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Figurinha não encontrada."
                        )
                );
    }

    private PosseFigurinha buscarPosse(
            String cpfUsuario,
            FigurinhaId figurinhaId
    ){
        return posseRepository
                .findByUsuarioCpfAndFigurinhaIdCodigoAndFigurinhaIdTipo(
                        cpfUsuario,
                        figurinhaId.getCodigo(),
                        figurinhaId.getTipo()
                )
                .orElse(null);
    }
}