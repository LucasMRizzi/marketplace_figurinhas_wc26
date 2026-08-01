package br.com.marketplace.entity;

import br.com.marketplace.entity.enums.StatusOferta;
import br.com.marketplace.entity.enums.TipoOferta;
import br.com.marketplace.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oferta")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oferta {

    /**
     * =========================================================
     * Variáveis
     * =========================================================
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oferta")
    private Integer idOferta;

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

    @Column(
            name = "valor_de_mercado",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal valorDeMercado;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(name = "prazo_limite", nullable = false)
    private LocalDate prazoLimite;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    /**
     * =========================================================
     * Chaves estrangeiras
     * =========================================================
     */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_proponente",
            referencedColumnName = "cpf",
            nullable = false
    )
    private Usuario usuarioProponente;

    /**
     * =========================================================
     * Relações
     * =========================================================
     */

    @OneToOne(
            mappedBy = "oferta",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Venda venda;

    @OneToOne(
            mappedBy = "oferta",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
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
    private List<ItemOfertado> itensOfertados = new ArrayList<>();

    /**
     * =========================================================
     * Métodos
     * =========================================================
     */

    public Oferta(
            TipoOferta tipo,
            Usuario usuarioProponente,
            LocalDate prazoLimite,
            String descricao
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

        validarPrazoLimite(prazoLimite);
        validarDescricao(descricao);

        this.tipo = tipo;
        this.usuarioProponente = usuarioProponente;
        this.status = StatusOferta.PENDENTE;
        this.dataCriacao = LocalDate.now();
        this.prazoLimite = prazoLimite;
        this.descricao = descricao;
    }

    public void atualizarOferta(
            LocalDate prazoLimite,
            String descricao
    ){
        validarPrazoLimite(prazoLimite);
        validarDescricao(descricao);

        this.prazoLimite = prazoLimite;
        this.descricao = descricao;
    }

    public boolean estaPendente() {
        return status == StatusOferta.PENDENTE;
    }

    public boolean ehVenda() {
        return tipo == TipoOferta.VENDA;
    }

    public boolean ehTroca() {
        return tipo == TipoOferta.TROCA;
    }

    public void concretizar() {

        if (!estaPendente()) {
            throw new IllegalStateException(
                    "Apenas ofertas pendentes podem ser concretizadas."
            );
        }

        this.status = StatusOferta.CONCRETIZADA;
    }

    public void expirar() {
        if (!estaPendente()) {
            throw new IllegalStateException(
                    "Apenas ofertas pendentes podem expirar."
            );
        }

        this.status = StatusOferta.EXPIRADA;
    }

    public void adicionarItemOfertado(ItemOfertado item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "O item ofertado é obrigatório."
            );
        }

        if (!estaPendente()) {
            throw new RegraDeNegocioException(
                    "Apenas ofertas pendentes podem receber itens."
            );
        }

        itensOfertados.add(item);
    }

    public void calcularValorDeMercado(){
        this.valorDeMercado = itensOfertados.stream()
                .map(ItemOfertado::calcularValorDeMercado)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public void associarTroca(Troca troca) {
        if (troca == null) {
            throw new IllegalArgumentException(
                    "A troca é obrigatória."
            );
        }

        if (!ehTroca()) {
            throw new IllegalStateException(
                    "Uma oferta do tipo VENDA não pode receber uma troca."
            );
        }

        if (this.venda != null) {
            throw new IllegalStateException(
                    "A oferta já possui uma venda associada."
            );
        }

        if (this.troca != null && this.troca != troca) {
            throw new IllegalStateException(
                    "A oferta já possui outra troca associada."
            );
        }

        this.troca = troca;
    }

    /**
     * =========================================================
     * Métodos Auxiliares
     * =========================================================
     */

    private void validarPrazoLimite(LocalDate prazoLimite) {
        if (prazoLimite == null) {
            throw new IllegalArgumentException(
                    "O prazo limite é obrigatório."
            );
        }

        if (prazoLimite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "O prazo limite não pode ser anterior à data atual."
            );
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null
                && descricao.length() > 150) {
            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 150 caracteres."
            );
        }
    }

}