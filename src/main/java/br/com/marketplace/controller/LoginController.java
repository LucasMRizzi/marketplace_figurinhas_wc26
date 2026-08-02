package br.com.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(
        name = "Autenticação",
        description = "Endpoints para login e geração de tokens de acesso."
)
public class LoginController {

    @GetMapping("/login")
    @Operation(
            summary = "Acessar tela de Login",
            description = "Retorna a view de autenticação padrão (pública).",
            security = {}
    )
    public String login() {
        return "login";
    }

}