package br.com.rts.eventmanager.financeiro.conta.entities;

import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONTA", schema = "FINANCEIRO")
@EntityListeners(AuditingEntityListener.class)
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

        @Builder.Default
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "instituicao_id", nullable = false, updatable = false)
    private Long instituicao;

    @Column(name = "evento_id", nullable = false, updatable = false)
    private Long evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false, updatable = false)
    private Venda venda;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoDevedor;

    @Column(nullable = false)
    private Boolean pago;

    private LocalDateTime dataPagamento;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;


}
