package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.StatusPropostaTroca;
import br.com.marketplace.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proposta_troca")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PropostaTroca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proposta")
    private Integer idProposta;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusPropostaTroca status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_oferta", nullable = false)
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante", referencedColumnName = "cpf", nullable = false)
    private Usuario solicitante;

    @OneToMany(mappedBy = "proposta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPropostaTroca> itens = new ArrayList<>();

    public PropostaTroca(Oferta oferta, Usuario solicitante) {
        if (oferta == null || solicitante == null) {
            throw new IllegalArgumentException("A oferta e o solicitante são obrigatórios.");
        }
        if (!oferta.ehTroca() || !oferta.estaPendente()) {
            throw new RegraDeNegocioException("Apenas ofertas de troca disponíveis recebem propostas.");
        }
        if (oferta.getUsuarioProponente().getCpf().equals(solicitante.getCpf())) {
            throw new RegraDeNegocioException("Você não pode enviar uma proposta para a própria oferta.");
        }
        this.oferta = oferta;
        this.solicitante = solicitante;
        this.status = StatusPropostaTroca.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    public void adicionarItem(ItemPropostaTroca item) {
        if (item == null) throw new IllegalArgumentException("O item é obrigatório.");
        itens.add(item);
    }

    public boolean estaPendente() { return status == StatusPropostaTroca.PENDENTE; }

    public void aceitar() {
        validarPendente();
        status = StatusPropostaTroca.ACEITA;
        dataResposta = LocalDateTime.now();
    }

    public void recusar() {
        validarPendente();
        status = StatusPropostaTroca.RECUSADA;
        dataResposta = LocalDateTime.now();
    }

    private void validarPendente() {
        if (!estaPendente()) throw new RegraDeNegocioException("A proposta já foi respondida.");
    }
}
