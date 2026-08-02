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

/**
 * Serviço responsável por gerenciar a ação de colar figurinhas nos álbuns dos usuários.
 * Garante as regras de negócio, como a posse prévia da figurinha, e mantém a 
 * porcentagem de completude do álbum (progresso) sempre atualizada.
 */
@Service
@RequiredArgsConstructor
public class FigurinhaColadaService {

    private final FigurinhaColadaRepository figurinhaColadaRepository;
    private final AlbumRepository albumRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final PosseFigurinhaRepository posseFigurinhaRepository;
    private final FigurinhaColadaMapper figurinhaColadaMapper;

    /**
     * Vincula uma figurinha a um álbum específico de um usuário, caracterizando o ato de "colar".
     * Antes de persistir, verifica se o álbum existe, se a figurinha existe no catálogo, 
     * se o usuário de fato possui a figurinha no inventário e se ela já não foi colada neste álbum.
     * Após o processo, a porcentagem de completude do álbum é recalculada.
     *
     * @param cpfUsuario CPF do dono do álbum.
     * @param nomeAlbum  Nome do álbum onde a figurinha será colada.
     * @param request    Objeto contendo o código e o tipo da figurinha a ser colada.
     * @return FigurinhaColadaResponse contendo os dados do registro criado.
     * @throws RecursoNaoEncontradoException Se o álbum ou a figurinha não existirem na base de dados.
     * @throws RegraDeNegocioException       Se o usuário tentar colar uma figurinha que ele não possui no inventário (PosseFigurinha).
     * @throws RecursoJaExisteException      Se a figurinha especificada já estiver colada neste álbum.
     */
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

    /**
     * Retorna uma lista com todas as figurinhas que já foram coladas em um determinado álbum.
     *
     * @param cpfUsuario CPF do dono do álbum.
     * @param nomeAlbum  Nome do álbum a ser consultado.
     * @return Lista de FigurinhaColadaResponse.
     * @throws RecursoNaoEncontradoException Se o álbum especificado não existir.
     */
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

    /**
     * Remove uma figurinha que estava colada em um álbum (ato de "descolar" ou remover registro incorreto).
     * Após a remoção, a porcentagem de completude do álbum é recalculada para baixo.
     *
     * @param cpfUsuario CPF do dono do álbum.
     * @param nomeAlbum  Nome do álbum de onde a figurinha será removida.
     * @param codigo     Código da figurinha a ser removida (ex: "BRA10").
     * @param tipo       Tipo da figurinha a ser removida (ex: NORMAL, BRILHANTE).
     * @throws RecursoNaoEncontradoException Se o álbum não existir ou se a figurinha especificada não estiver colada nele.
     */
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

    /**
     * Método utilitário para centralizar a busca por um Álbum e padronizar
     * a exceção lançada caso ele não seja encontrado.
     *
     * @param cpfUsuario CPF do dono do álbum.
     * @param nomeAlbum  Nome do álbum procurado.
     * @return A entidade Album instanciada.
     * @throws RecursoNaoEncontradoException Se o álbum não existir.
     */
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

    /**
     * Calcula o percentual de preenchimento do álbum com base no número total de figurinhas
     * disponíveis no catálogo do sistema em relação à quantidade que o usuário já colou.
     * Atualiza a propriedade 'completude' da entidade Album.
     *
     * @param album O álbum que terá sua porcentagem de completude recalculada.
     */
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