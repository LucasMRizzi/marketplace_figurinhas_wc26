package br.com.marketplace.controller;

import br.com.marketplace.service.OfertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final OfertaService ofertaService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
                "ofertas",
                ofertaService.listar(null, null)
        );

        return "dashboard/dashboard";
    }

}