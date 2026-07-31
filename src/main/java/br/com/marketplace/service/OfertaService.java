package br.com.marketplace.service;

import br.com.marketplace.dto.oferta.OfertaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.OfertaMapper;
import br.com.marketplace.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final OfertaMapper ofertaMapper;

    @Transactional(readOnly = true)
    public OfertaResponse buscar(Integer idOferta) {
        return ofertaMapper.toResponse(
                buscarEntidade(idOferta)
        );
    }

    @Transactional(readOnly = true)
    public List<OfertaResponse> listar(
            TipoOferta tipo,
            StatusOferta status
    ) {
        List<Oferta> ofertas;

        if (tipo != null && status != null) {
            ofertas = ofertaRepository
                    .findByTipoAndStatus(tipo, status);
        } else if (tipo != null) {
            ofertas = ofertaRepository.findByTipo(tipo);
        } else if (status != null) {
            ofertas = ofertaRepository.findByStatus(status);
        } else {
            ofertas = ofertaRepository.findAll();
        }

        return ofertas.stream()
                .map(ofertaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfertaResponse> listarPorUsuario(
            String cpf
    ) {
        return ofertaRepository
                .findByUsuarioProponenteCpf(cpf)
                .stream()
                .map(ofertaMapper::toResponse)
                .toList();
    }

    @Transactional
    public OfertaResponse expirar(Integer idOferta) {
        Oferta oferta = buscarEntidade(idOferta);

        oferta.expirar();

        return ofertaMapper.toResponse(oferta);
    }

    private Oferta buscarEntidade(Integer idOferta) {
        return ofertaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Oferta não encontrada."
                        )
                );
    }
}