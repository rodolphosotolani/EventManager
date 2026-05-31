package br.com.rts.eventmanager.financeiro.fluxocaixa.entities;

import br.com.rts.eventmanager.financeiro.caixa.entities.Caixa;
import br.com.rts.eventmanager.financeiro.fluxocaixa.enumerators.TipoFluxoCaixaEnum;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FLUXO_CAIXA", schema = "FINANCEIRO")
@EntityListeners(AuditingEntityListener.class)
public class FluxoCaixa {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "instituicao_id", nullable = false, updatable = false)
    private Long instituicao;

    @Column(name = "evento_id", nullable = false, updatable = false)
    private Long evento;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caixa_id")
    private Caixa caixa;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @Enumerated(EnumType.STRING)
    private TipoFluxoCaixaEnum tipoFluxoCaixa;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento")
    private FormaPagamentoEnum formaPagamento;

    private String observacao;

    private LocalDateTime dataVenda;

    @Column(name = "usuario_id")
    private Long usuario;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
