package br.com.marketplace.service;

import br.com.marketplace.dto.usuario.AtualizarUsuarioRequest;
import br.com.marketplace.dto.usuario.CriarUsuarioRequest;
import br.com.marketplace.dto.usuario.UsuarioResponse;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.UsuarioMapper;
import br.com.marketplace.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pelo gerenciamento de contas de usuários no marketplace.
 * Lida com o cadastro, busca, atualização de dados cadastrais e remoção dos usuários,
 * garantindo a unicidade do CPF na plataforma.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    /**
     * Cadastra um novo usuário na plataforma.
     * Antes de persistir os dados, verifica se já existe uma conta vinculada ao CPF informado.
     *
     * @param request Objeto contendo os dados de registro do usuário.
     * @return UsuarioResponse contendo os dados do usuário recém-criado.
     * @throws RecursoJaExisteException Se o CPF já estiver cadastrado no banco de dados.
     */
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criar(CriarUsuarioRequest request) {
        if (usuarioRepository.existsById(request.cpf())) {
            throw new RecursoJaExisteException(
                    "Já existe um usuário com esse CPF."
            );
        }

        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new RecursoJaExisteException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        String senhaCodificada = passwordEncoder.encode(request.senha());

        Usuario usuario = usuarioMapper.toEntity(request, senhaCodificada);

        Usuario usuario_salvo = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario_salvo);
    }

    /**
     * Recupera as informações detalhadas de um usuário utilizando seu CPF (Chave Primária).
     *
     * @param cpf CPF do usuário procurado.
     * @return UsuarioResponse com os dados formatados do usuário.
     * @throws RecursoNaoEncontradoException Se não existir nenhum usuário com o CPF informado.
     */
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorCpf(String cpf){
        return usuarioMapper.toResponse(buscarEntidadePorCpf(cpf));
    }

    /**
     * Retorna uma lista contendo todos os usuários cadastrados na plataforma.
     *
     * @return Lista de UsuarioResponse.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    /**
     * Atualiza os dados cadastrais (editáveis) de um usuário existente.
     *
     * @param cpf     CPF do usuário que será atualizado.
     * @param request Objeto contendo os novos dados do usuário.
     * @return UsuarioResponse com as informações atualizadas.
     * @throws RecursoNaoEncontradoException Se o usuário não for localizado.
     */
    @Transactional
    public UsuarioResponse atualizar(
            String cpf,
            AtualizarUsuarioRequest request
    ) {
        Usuario usuario = buscarEntidadePorCpf(cpf);

        if (!usuario.getEmail().equals(request.email())
                && usuarioRepository.existsByEmailIgnoreCase(request.email())) {

            throw new RecursoJaExisteException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        usuarioMapper.updateEntity(usuario, request);

        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Remove fisicamente um usuário do sistema a partir do seu CPF.
     * A depender das configurações de cascata (Cascade) na entidade, isso também pode 
     * apagar álbuns, ofertas e outras dependências atreladas a ele.
     *
     * @param cpf CPF do usuário a ser deletado.
     * @throws RecursoNaoEncontradoException Se o usuário não for encontrado para exclusão.
     */
    @Transactional
    public void remover(String cpf){
        Usuario usuario = buscarEntidadePorCpf(cpf);
        usuarioRepository.delete(usuario);
    }

    /**
     * Método utilitário privado para encapsular a busca de um usuário no banco
     * e o lançamento padrão da exceção caso ele não exista.
     *
     * @param cpf CPF do usuário.
     * @return A entidade Usuario recuperada do banco de dados.
     * @throws RecursoNaoEncontradoException Se o usuário não for encontrado.
     */
    private Usuario buscarEntidadePorCpf(String cpf){
        return usuarioRepository.findById(cpf)
                .orElseThrow(()->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }
}