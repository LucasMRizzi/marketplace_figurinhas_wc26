package br.com.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({
            "/",
            "/login",
            "/registro",
            "/ofertas",
            "/ofertas/**",
            "/inventario",
            "/colecao",
            "/negociacoes",
            "/negociacoes/**",
            "/perfil"
    })
    public String frontend() {
        return "forward:/index.html";
    }

}