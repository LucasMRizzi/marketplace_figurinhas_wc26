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

@Service
@RequiredArgsConstructor
public class DesejaFigurinhaService {

    private final DesejaFigurinhaRepository desejoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FigurinhaRepository figurinhaRepository;
    private final DesejaFigurinhaMapper desejoMapper;

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