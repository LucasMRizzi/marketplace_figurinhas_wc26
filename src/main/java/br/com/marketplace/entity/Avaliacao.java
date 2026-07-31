package br.com.marketplace.entity;

import br.com.marketplace.entity.id.AvaliacaoId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "avaliacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Avaliacao {

    @EmbeddedId
    private AvaliacaoId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("cpfAvaliador")
    @JoinColumn(
            name = "usuario_avaliador",
            referencedColumnName = "cpf",
            nullable = false
    )
    private Usuario usuarioAvaliador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("cpfAvaliado")
    @JoinColumn(
            name = "usuario_avaliado",
            referencedColumnName = "cpf",
            nullable = false
    )
    private Usuario usuarioAvaliado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idConcretizacao")
    @JoinColumn(
            name = "id_concretizacao",
            referencedColumnName = "id_concretizacao",
            nullable = false
    )
    private Concretizacao concretizacao;

    @Column(
            name = "nota",
            nullable = false,
            precision = 3,
            scale = 2
    )
    private BigDecimal nota;

    @Column(name = "comentario", length = 150)
    private String comentario;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    public Avaliacao(
            Usuario usuarioAvaliador,
            Usuario usuarioAvaliado,
            Concretizacao concretizacao,
            BigDecimal nota,
            String comentario
    ) {
        validarParticipantes(
                usuarioAvaliador,
                usuarioAvaliado,
                concretizacao
        );

        validarNota(nota);
        validarComentario(comentario);

        if (concretizacao.getIdConcretizacao() == null) {
            throw new IllegalStateException(
                    "A concretização precisa estar salva antes de ser avaliada."
            );
        }

        this.id = new AvaliacaoId(
                usuarioAvaliador.getCpf(),
                usuarioAvaliado.getCpf(),
                concretizacao.getIdConcretizacao()
        );

        this.usuarioAvaliador = usuarioAvaliador;
        this.usuarioAvaliado = usuarioAvaliado;
        this.concretizacao = concretizacao;
        this.nota = nota;
        this.comentario = comentario;
        this.data = LocalDate.now();
    }

    public void alterarAvaliacao(
            BigDecimal novaNota,
            String novoComentario
    ) {
        validarNota(novaNota);
        validarComentario(novoComentario);

        this.nota = novaNota;
        this.comentario = novoComentario;
    }

    private void validarNota(BigDecimal nota) {
        if (nota == null
                || nota.compareTo(BigDecimal.ZERO) < 0
                || nota.compareTo(
                new BigDecimal("5.00")
        ) > 0) {
            throw new IllegalArgumentException(
                    "A nota deve estar entre 0 e 5."
            );
        }
    }

    private void validarComentario(String comentario) {
        if (comentario != null
                && comentario.length() > 150) {
            throw new IllegalArgumentException(
                    "O comentário deve possuir no máximo 150 caracteres."
            );
        }
    }

    private void validarParticipantes(
            Usuario avaliador,
            Usuario avaliado,
            Concretizacao concretizacao
    ) {
        if (avaliador == null || avaliado == null) {
            throw new IllegalArgumentException(
                    "Avaliador e avaliado são obrigatórios."
            );
        }

        if (concretizacao == null) {
            throw new IllegalArgumentException(
                    "A concretização é obrigatória."
            );
        }

        if (avaliador.getCpf().equals(avaliado.getCpf())) {
            throw new IllegalArgumentException(
                    "Um usuário não pode avaliar a si mesmo."
            );
        }

        Usuario proponente =
                concretizacao
                        .getOferta()
                        .getUsuarioProponente();

        Usuario aceitante =
                concretizacao.getAceitante();

        boolean proponenteAvaliaAceitante =
                avaliador.getCpf().equals(proponente.getCpf())
                        && avaliado.getCpf()
                        .equals(aceitante.getCpf());

        boolean aceitanteAvaliaProponente =
                avaliador.getCpf().equals(aceitante.getCpf())
                        && avaliado.getCpf()
                        .equals(proponente.getCpf());

        if (!proponenteAvaliaAceitante
                && !aceitanteAvaliaProponente) {
            throw new IllegalArgumentException(
                    "A avaliação deve ocorrer entre os participantes da negociação."
            );
        }
    }
}