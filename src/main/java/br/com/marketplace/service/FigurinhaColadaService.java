package br.com.marketplace.service;

import br.com.marketplace.dto.figurinhaColada.CriarFigurinhaColadaRequest;
import br.com.marketplace.dto.figurinhaColada.FigurinhaColadaResponse;
import br.com.marketplace.entity.Album;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.FigurinhaColada;
import br.com.marketplace.entity.enums.TipoFigurinha;
import br.com.marketplace.entity.id.AlbumId;
import br.com.marketplace.entity.id.FigurinhaColadaId;
import br.com.marketplace.entity.id.FigurinhaId;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.exception.RegraDeNegocioException;
import br.com.marketplace.mapper.FigurinhaColadaMapper;
import br.com.marketplace.repository.AlbumRepository;
import br.com.marketplace.repository.FigurinhaColadaRepository;
import br.com.marketplace.repository.FigurinhaRepository;
import br.com.marketplace.repository.PosseFigurinhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FigurinhaColadaService {

    private final FigurinhaColadaRepository figurinhaColadaRepository;
    private final AlbumRepository albumRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final PosseFigurinhaRepository posseFigurinhaRepository;
    private final FigurinhaColadaMapper figurinhaColadaMapper;

    @Transactional
    public FigurinhaColadaResponse colar(
            String cpfUsuario,
            String nomeAlbum,
            CriarFigurinhaColadaRequest request
    ) {
        Album album = buscarAlbum(
                cpfUsuario,
                nomeAlbum
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

        boolean possui = posseFigurinhaRepository
                .existsByUsuarioCpfAndFigurinhaId(
                        cpfUsuario,
                        figurinhaId
                );

        if (!possui) {
            throw new RegraDeNegocioException(
                    "O usuário não possui essa figurinha."
            );
        }

        FigurinhaColadaId id = new FigurinhaColadaId(
                figurinha.getCodigo(),
                figurinha.getTipo(),
                album.getId()
        );

        if (figurinhaColadaRepository.existsById(id)) {
            throw new RecursoJaExisteException(
                    "Essa figurinha já está colada no álbum."
            );
        }

        FigurinhaColada colada =
                figurinhaColadaMapper.toEntity(
                        album,
                        figurinha
                );

        FigurinhaColada salva =
                figurinhaColadaRepository.save(colada);

        recalcularCompletude(album);

        return figurinhaColadaMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<FigurinhaColadaResponse> listar(
            String cpfUsuario,
            String nomeAlbum
    ) {
        buscarAlbum(cpfUsuario, nomeAlbum);

        return figurinhaColadaRepository
                .findByAlbum_Id_NomeAndAlbum_Id_Usuario(
                        nomeAlbum,
                        cpfUsuario
                )
                .stream()
                .map(figurinhaColadaMapper::toResponse)
                .toList();
    }

    @Transactional
    public void remover(
            String cpfUsuario,
            String nomeAlbum,
            String codigo,
            TipoFigurinha tipo
    ) {
        Album album = buscarAlbum(
                cpfUsuario,
                nomeAlbum
        );

        FigurinhaColadaId id = new FigurinhaColadaId(
                codigo,
                tipo,
                album.getId()
        );

        FigurinhaColada colada =
                figurinhaColadaRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Figurinha colada não encontrada."
                                )
                        );

        figurinhaColadaRepository.delete(colada);
        figurinhaColadaRepository.flush();

        recalcularCompletude(album);
    }

    private Album buscarAlbum(
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

    private void recalcularCompletude(Album album) {
        long totalFigurinhas =
                figurinhaRepository.count();

        long totalColadas =
                figurinhaColadaRepository
                        .countByAlbum_Id_NomeAndAlbum_Id_Usuario(
                                album.getNome(),
                                album.getCpfUsuario()
                        );

        BigDecimal novaCompletude = BigDecimal
                .valueOf(totalColadas)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(totalFigurinhas),
                        2,
                        RoundingMode.HALF_UP
                );

        album.atualizarCompletude(
                novaCompletude
        );
    }
}