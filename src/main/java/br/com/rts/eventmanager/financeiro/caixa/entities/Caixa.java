package br.com.rts.eventmanager.financeiro.caixa.entities;

import br.com.rts.eventmanager.financeiro.caixa.enumerators.StatusCaixaEnum;
import br.com.rts.eventmanager.financeiro.fluxocaixa.entities.FluxoCaixa;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CAIXA", schema = "FINANCEIRO")
@EntityListeners(AuditingEntityListener.class)
public class Caixa {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "instituicao_id", nullable = false, updatable = false)
    private Long instituicao;

    @Column(name = "evento_id", nullable = false, updatable = false)
    private Long evento;

    @Column(nullable = false, updatable = false)
    private Long usuarioAbertura;

    private Long usuarioFechamento;

    @Enumerated(EnumType.STRING)
    private StatusCaixaEnum statusCaixa;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoInicial;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldoFinalCalculado;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldoFinalInformado;

    @Column(precision = 10, scale = 2)
    private BigDecimal diferencaCaixa;

    @Builder.Default
    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FluxoCaixa> fluxoCaixas = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

}
