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

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlbumMapper albumMapper;

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

    @Transactional(readOnly = true)
    public AlbumResponse buscar(
            String cpfUsuario,
            String nomeAlbum
    ) {
        return albumMapper.toResponse(
                buscarEntidade(cpfUsuario, nomeAlbum)
        );
    }

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

    @Transactional(readOnly = true)
    public List<AlbumResponse> listarTodos() {
        return albumRepository.findAll()
                .stream()
                .map(albumMapper::toResponse)
                .toList();
    }

    @Transactional
    public void remover(
            String cpfUsuario,
            String nomeAlbum
    ) {
        Album album =
                buscarEntidade(cpfUsuario, nomeAlbum);

        albumRepository.delete(album);
    }

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