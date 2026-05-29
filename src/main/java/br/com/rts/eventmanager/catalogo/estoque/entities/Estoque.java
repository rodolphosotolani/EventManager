package br.com.rts.eventmanager.catalogo.estoque.entities;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ESTOQUE", schema = "CATALOGO")
@EntityListeners(AuditingEntityListener.class)
public class Estoque {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Builder.Default
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false, updatable = false)
    private Long instituicao;

    @Column(nullable = false, updatable = false)
    private Long evento;

    @ManyToOne()
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false, updatable = false)
    private Integer quantidadeInicial;

    @Column(nullable = false)
    private Integer quantidadeAtual;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorCompraUnitario;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    @Builder.Default
    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movimentacao> movimentacoes = new ArrayList<>();

}
