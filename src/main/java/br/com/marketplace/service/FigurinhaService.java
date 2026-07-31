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


@Service
@RequiredArgsConstructor
public class FigurinhaService {

    private final FigurinhaRepository figurinhaRepository;
    private final FigurinhaMapper figurinhaMapper;

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

    @Transactional(readOnly = true)
    public FigurinhaResponse buscar(
            String codigo,
            TipoFigurinha tipo
    ) {
        return figurinhaMapper.toResponse(
                buscarEntidade(codigo, tipo)
        );
    }
    @Transactional(readOnly = true)
    public List<FigurinhaResponse> listarTodos() {
        return figurinhaRepository.findAll()
                .stream()
                .map(figurinhaMapper::toResponse)
                .toList();
    }

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

    @Transactional
    public void remover(
            String codigo,
            TipoFigurinha tipo
    ) {
        Figurinha figurinha =
                buscarEntidade(codigo, tipo);

        figurinhaRepository.delete(figurinha);
    }

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