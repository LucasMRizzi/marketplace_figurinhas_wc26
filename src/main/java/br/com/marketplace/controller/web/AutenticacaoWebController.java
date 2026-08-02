package br.com.marketplace.controller.web;

import br.com.marketplace.dto.usuario.CriarUsuarioRequest;
import br.com.marketplace.exception.RecursoJaExisteException;
import br.com.marketplace.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AutenticacaoWebController {

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @Valid CriarUsuarioRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/registro";
        }

        try {
            usuarioService.criar(request);
        } catch (RecursoJaExisteException exception) {
            bindingResult.reject(
                    "usuario.existente",
                    exception.getMessage()
            );

            return "auth/registro";
        }

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Conta criada. Agora faça login."
        );

        return "redirect:/login";
    }
}