package br.com.marketplace.service;

import br.com.marketplace.dto.troca.AtualizarTrocaRequest;
import br.com.marketplace.dto.troca.CriarTrocaRequest;
import br.com.marketplace.dto.troca.TrocaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Troca;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.Venda;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.TrocaMapper;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.TrocaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrocaService {

    private final TrocaRepository trocaRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TrocaMapper trocaMapper;

    @Transactional
    public TrocaResponse criar(
            String cpfProponente,
            CriarTrocaRequest request
    ) {
        Usuario proponente = buscarUsuario(cpfProponente);

        Oferta oferta = new Oferta(
                TipoOferta.TROCA,
                proponente
        );

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        Troca troca = new Troca(
                ofertaSalva,
                request.prazoLimite(),
                request.descricao()
        );

        Troca trocaSalva = trocaRepository.save(troca);

        return trocaMapper.toResponse(trocaSalva);
    }

    @Transactional(readOnly = true)
    public TrocaResponse buscar(Integer idOferta) {
        return trocaMapper.toResponse(
                buscarEntidade(idOferta)
        );
    }

    @Transactional(readOnly = true)
    public List<TrocaResponse> listarTodas() {
        return trocaRepository.findAll()
                .stream()
                .map(trocaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrocaResponse> listarPorProponente(
            String cpf
    ) {
        return trocaRepository
                .findByOfertaUsuarioProponenteCpf(cpf)
                .stream()
                .map(trocaMapper::toResponse)
                .toList();
    }

    @Transactional
    public void atualizar(
            Integer idOferta,
            AtualizarTrocaRequest request
    ) {
        Troca troca = buscarEntidade(idOferta);

        troca.atualizar(
                request.prazoLimite(),
                request.descricao()
        );
    }

    @Transactional
    public void remover(Integer idOferta) {
        Troca troca = buscarEntidade(idOferta);

        ofertaRepository.delete(
                troca.getOferta()
        );
    }

    private Troca buscarEntidade(Integer idOferta) {
        return trocaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Troca não encontrada."
                        )
                );
    }

    private Usuario buscarUsuario(String cpf) {
        return usuarioRepository.findById(cpf)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }
}