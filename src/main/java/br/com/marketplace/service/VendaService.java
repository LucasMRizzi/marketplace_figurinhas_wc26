package br.com.marketplace.service;

import br.com.marketplace.dto.venda.AtualizarVendaRequest;
import br.com.marketplace.dto.venda.CriarVendaRequest;
import br.com.marketplace.dto.venda.VendaResponse;
import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.Usuario;
import br.com.marketplace.entity.Venda;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RecursoNaoEncontradoException;
import br.com.marketplace.mapper.VendaMapper;
import br.com.marketplace.repository.OfertaRepository;
import br.com.marketplace.repository.UsuarioRepository;
import br.com.marketplace.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VendaMapper vendaMapper;

    @Transactional
    public VendaResponse criar(
            String cpfProponente,
            CriarVendaRequest request
    ) {
        Usuario proponente = buscarUsuario(cpfProponente);

        Oferta oferta = new Oferta(
                TipoOferta.VENDA,
                proponente,
                request.prazoLimite(),
                request.descricao()
        );

        Oferta ofertaSalva = ofertaRepository.save(oferta);

        Venda venda = new Venda(
                ofertaSalva,
                request.precoUnitario(),
                request.quantidade()
        );

        Venda vendaSalva = vendaRepository.save(venda);

        return vendaMapper.toResponse(vendaSalva);
    }

    @Transactional(readOnly = true)
    public VendaResponse buscar(Integer idOferta) {
        return vendaMapper.toResponse(
                buscarEntidade(idOferta)
        );
    }

    @Transactional(readOnly = true)
    public List<VendaResponse> listarTodas() {
        return vendaRepository.findAll()
                .stream()
                .map(vendaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VendaResponse> listarPorProponente(
            String cpf
    ) {
        return vendaRepository
                .findByOfertaUsuarioProponenteCpf(cpf)
                .stream()
                .map(vendaMapper::toResponse)
                .toList();
    }

    @Transactional
    public VendaResponse atualizar(
            Integer idOferta,
            AtualizarVendaRequest request
    ) {
        Venda venda = buscarEntidade(idOferta);

        venda.atualizarVenda(request.precoUnitario(), request.quantidade());

        return vendaMapper.toResponse(venda);
    }

    @Transactional
    public void remover(Integer idOferta) {
        Venda venda = buscarEntidade(idOferta);

        ofertaRepository.delete(
                venda.getOferta()
        );
    }

    private Venda buscarEntidade(Integer idOferta) {
        return vendaRepository.findById(idOferta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Venda não encontrada."
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