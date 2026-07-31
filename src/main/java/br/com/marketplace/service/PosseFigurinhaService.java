package br.com.marketplace.service;

import br.com.marketplace.dto.posseFigurinha.CriarPosseFigurinhaRequest;
import br.com.marketplace.dto.posseFigurinha.PosseFigurinhaResponse;
import br.com.marketplace.entity.Figurinha;
import br.com.marketplace.entity.PosseFigurinha;
import br.com.marketplace.entity.Usuario;
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

@Service
@RequiredArgsConstructor
public class PosseFigurinhaService {

    private final PosseFigurinhaRepository posseRepository;
    private final UsuarioRepository usuarioRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final PosseFigurinhaMapper posseMapper;

    @Transactional
    public PosseFigurinhaResponse adicionar(
            String cpfUsuario,
            CriarPosseFigurinhaRequest request
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

        PosseFigurinha posse = posseRepository
                .findByUsuarioCpfAndFigurinhaIdCodigoAndFigurinhaIdTipo(
                        cpfUsuario,
                        request.codigoFigurinha(),
                        request.tipoFigurinha()
                )
                .orElse(null);

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

    @Transactional(readOnly = true)
    public PosseFigurinhaResponse buscar(Integer idPosse) {
        return posseMapper.toResponse(
                buscarEntidade(idPosse)
        );
    }

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

    @Transactional
    public void remover(Integer idPosse) {
        PosseFigurinha posse =
                buscarEntidade(idPosse);

        posseRepository.delete(posse);
    }

    private PosseFigurinha buscarEntidade(Integer idPosse) {
        return posseRepository.findById(idPosse)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Posse não encontrada."
                        )
                );
    }
}