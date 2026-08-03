package br.com.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(
        name = "Início",
        description = "Endpoints para interface home."
)
public class HomeController {

    @GetMapping("/")
    @Operation(
            summary = "Acessar tela Home",
            description = "Retorna a view da tela inicial.",
            security = {}
    )
    public String home() {
        return "index";
    }

}