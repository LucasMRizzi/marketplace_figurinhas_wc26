package br.com.marketplace.service;

import br.com.marketplace.entity.Oferta;
import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.repository.OfertaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfertaExpiracaoService {

    private final OfertaRepository ofertaRepository;

    public OfertaExpiracaoService(OfertaRepository ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    // Roda a cada 1 hora (em milissegundos: 3600000)
    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void verificarEExpirarOfertas() {
        LocalDateTime hoje = LocalDateTime.now();
    
        // Busca ofertas PENDENTES onde o prazoLimite é menor que hoje
        List<Oferta> ofertasVencidas = ofertaRepository.findByStatusAndPrazoLimiteBefore(StatusOferta.PENDENTE, hoje);

        if (!ofertasVencidas.isEmpty()) {
            for (Oferta oferta : ofertasVencidas) {
                oferta.expirar();
            }
            ofertaRepository.saveAll(ofertasVencidas);
            System.out.println("Foram expiradas " + ofertasVencidas.size() + " ofertas.");
        }
    }
}