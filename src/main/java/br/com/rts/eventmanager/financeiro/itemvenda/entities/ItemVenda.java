package br.com.rts.eventmanager.financeiro.itemvenda.entities;

import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ITEM_VENDA", schema = "FINANCEIRO")
@EntityListeners(AuditingEntityListener.class)
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "instituicao_id", nullable = false, updatable = false)
    private Long instituicao;

    @Column(name = "evento_id", nullable = false, updatable = false)
    private Long evento;

    @Column(name = "produto_id", nullable = false)
    private Long produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;


}

