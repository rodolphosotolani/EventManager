package br.com.rts.eventmanager.catalogo.movimentacao.entities;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.MotivoMovimentacaoEnum;
import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.TipoMovimentacaoEnum;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MOVIMENTACAO", schema = "CATALOGO")
@EntityListeners(AuditingEntityListener.class)
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private Long instituicao;

    @Column(nullable = false)
    private Long evento;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estoque_id")
    private Estoque estoque;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "produtos_id")
    private Produto produto;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMovimentacaoEnum tipoMovimentacao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MotivoMovimentacaoEnum motivoMovimentacao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = false)
    private LocalDateTime dataMovimentacao;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
