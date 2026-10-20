package br.com.marketplace.controller;

import br.com.marketplace.dto.propostaTroca.CriarPropostaTrocaRequest;
import br.com.marketplace.dto.propostaTroca.PropostaTrocaResponse;
import br.com.marketplace.security.UsuarioDetails;
import br.com.marketplace.service.PropostaTrocaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PropostaTrocaController {
    private final PropostaTrocaService service;

    @PostMapping("/api/ofertas/{idOferta}/propostas-troca")
    @ResponseStatus(HttpStatus.CREATED)
    public PropostaTrocaResponse criar(@PathVariable Integer idOferta,
            @AuthenticationPrincipal UsuarioDetails usuario,
            @Valid @RequestBody CriarPropostaTrocaRequest request) {
        return service.criar(idOferta, usuario.getUsuario(), request);
    }

    @GetMapping("/api/propostas-troca/recebidas")
    public List<PropostaTrocaResponse> recebidas(@AuthenticationPrincipal UsuarioDetails usuario) {
        return service.listarRecebidas(usuario.getUsuario().getCpf());
    }

    @GetMapping("/api/propostas-troca/enviadas")
    public List<PropostaTrocaResponse> enviadas(@AuthenticationPrincipal UsuarioDetails usuario) {
        return service.listarEnviadas(usuario.getUsuario().getCpf());
    }

    @GetMapping("/api/propostas-troca/{idProposta}")
    public PropostaTrocaResponse buscar(@PathVariable Integer idProposta,
            @AuthenticationPrincipal UsuarioDetails usuario) {
        return service.buscar(idProposta, usuario.getUsuario());
    }

    @PatchMapping("/api/propostas-troca/{idProposta}/aceitar")
    public PropostaTrocaResponse aceitar(@PathVariable Integer idProposta,
            @AuthenticationPrincipal UsuarioDetails usuario) {
        return service.aceitar(idProposta, usuario.getUsuario());
    }

    @PatchMapping("/api/propostas-troca/{idProposta}/recusar")
    public PropostaTrocaResponse recusar(@PathVariable Integer idProposta,
            @AuthenticationPrincipal UsuarioDetails usuario) {
        return service.recusar(idProposta, usuario.getUsuario());
    }
}
