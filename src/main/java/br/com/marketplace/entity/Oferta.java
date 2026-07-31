package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oferta")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oferta")
    private Long idOferta;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "status_oferta"
    )
    private StatusOferta status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "tipo",
            nullable = false,
            columnDefinition = "tipo_oferta"
    )
    private TipoOferta tipo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_proponente",
            referencedColumnName = "cpf",
            nullable = false
    )
    private Usuario usuarioProponente;

    @OneToOne(
            mappedBy = "oferta",
            fetch = FetchType.LAZY
    )
    private Venda venda;

    @OneToOne(
            mappedBy = "oferta",
            fetch = FetchType.LAZY
    )
    private Troca troca;

    @OneToOne(
            mappedBy = "oferta",
            fetch = FetchType.LAZY
    )
    private Concretizacao concretizacao;

    @OneToMany(
            mappedBy = "oferta",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemOfertado> itensOfertados =
            new ArrayList<>();

    public Oferta(
            TipoOferta tipo,
            Usuario usuarioProponente
    ) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo da oferta é obrigatório."
            );
        }

        if (usuarioProponente == null) {
            throw new IllegalArgumentException(
                    "O usuário proponente é obrigatório."
            );
        }

        this.tipo = tipo;
        this.usuarioProponente = usuarioProponente;
        this.status = StatusOferta.Pendente;
        this.dataCriacao = LocalDate.now();
    }

    public boolean estaPendente() {
        return status == StatusOferta.Pendente;
    }

    public void concretizar() {
        if (!estaPendente()) {
            throw new IllegalStateException(
                    "Apenas ofertas pendentes podem ser concretizadas."
            );
        }

        this.status = StatusOferta.Concretizada;
    }

    public void expirar() {
        if (!estaPendente()) {
            throw new IllegalStateException(
                    "Apenas ofertas pendentes podem expirar."
            );
        }

        this.status = StatusOferta.Expirada;
    }
}