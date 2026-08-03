package br.com.marketplace.service;

import br.com.marketplace.dto.album.AlbumResponse;
import br.com.marketplace.dto.album.CriarAlbumRequest;
import br.com.marketplace.entity.Album;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.id.AlbumId;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.AlbumMapper;
import br.com.marketplace.repository.AlbumRepository;
import br.com.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por orquestrar as regras de negócio relacionadas à gestão
 * de álbuns de figurinhas dentro do marketplace. Garante a integridade das 
 * operações antes da persistência no banco de dados.
 */

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlbumMapper albumMapper;

     /**
     * Registra um novo álbum no sistema e o vincula a um usuário específico.
     *
     * @param cpfUsuario CPF do usuário que será o dono do álbum.
     * @param request    Objeto de transferência contendo os dados necessários para criar o álbum.
     * @return AlbumResponse contendo os dados do álbum recém-salvo.
     * @throws RecursoNaoEncontradoException Se o CPF não pertencer a um usuário cadastrado na base.
     * @throws RecursoJaExisteException      Se o usuário já possuir um álbum registrado com o mesmo nome.
     */
    @Transactional
    public AlbumResponse criar(
            String cpfUsuario,
            CriarAlbumRequest request
    ) {
        Usuario usuario = usuarioRepository
                .findById(cpfUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );

        AlbumId albumId = new AlbumId(
                request.nome(),
                cpfUsuario
        );

        if (albumRepository.existsById(albumId)) {
            throw new RecursoJaExisteException(
                    "O usuário já possui um álbum com esse nome."
            );
        }

        Album album = albumMapper.toEntity(
                request,
                usuario
        );

        Album salvo = albumRepository.save(album);

        return albumMapper.toResponse(salvo);
    }

    /**
     * Recupera os dados de um álbum específico, identificado pela combinação do usuário dono e do nome do álbum.
     *
     * @param cpfUsuario CPF do usuário dono do álbum.
     * @param nomeAlbum  Nome do álbum a ser buscado.
     * @return AlbumResponse com as informações formatadas do álbum.
     * @throws RecursoNaoEncontradoException Se a combinação de usuário e nome de álbum não existir no banco.
     */
    @Transactional(readOnly = true)
    public AlbumResponse buscar(
            String cpfUsuario,
            String nomeAlbum
    ) {
        return albumMapper.toResponse(
                buscarEntidade(cpfUsuario, nomeAlbum)
        );
    }

    /**
     * Retorna uma coleção contendo todos os álbuns criados por um determinado usuário.
     *
     * @param cpfUsuario CPF do usuário a ser consultado.
     * @return Lista de AlbumResponse pertencentes ao usuário.
     * @throws RecursoNaoEncontradoException Se o CPF fornecido não existir na base de usuários.
     */
    @Transactional(readOnly = true)
    public List<AlbumResponse> listarPorUsuario(
            String cpfUsuario
    ) {
        if (!usuarioRepository.existsById(cpfUsuario)) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        return albumRepository
                .findByUsuarioCpf(cpfUsuario)
                .stream()
                .map(albumMapper::toResponse)
                .toList();
    }

    /**
     * Varre a base de dados e devolve todos os álbuns registrados na plataforma.
     *
     * @return Lista contendo todos os álbuns cadastrados convertidos para AlbumResponse.
     */
    @Transactional(readOnly = true)
    public List<AlbumResponse> listarTodos() {
        return albumRepository.findAll()
                .stream()
                .map(albumMapper::toResponse)
                .toList();
    }

    /**
     * Exclui de forma definitiva um álbum do banco de dados.
     *
     * @param cpfUsuario CPF do usuário dono do álbum.
     * @param nomeAlbum  Nome do álbum a ser removido.
     * @throws RecursoNaoEncontradoException Se o álbum não for encontrado para exclusão.
     */
    @Transactional
    public void remover(
            String cpfUsuario,
            String nomeAlbum
    ) {
        Album album =
                buscarEntidade(cpfUsuario, nomeAlbum);

        albumRepository.delete(album);
    }

    /**
     * Método utilitário de uso interno para centralizar a lógica de busca de uma entidade Album
     * e o tratamento padronizado de erro caso ela não exista.
     *
     * @param cpfUsuario CPF do usuário dono do álbum.
     * @param nomeAlbum  Nome do álbum a ser buscado.
     * @return A entidade Album recuperada do banco de dados.
     * @throws RecursoNaoEncontradoException Se o álbum não for encontrado.
     */
    private Album buscarEntidade(
            String cpfUsuario,
            String nomeAlbum
    ) {
        AlbumId id = new AlbumId(
                nomeAlbum,
                cpfUsuario
        );

        return albumRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Álbum não encontrado."
                        )
                );
    }
}