package br.com.rts.eventmanager.catalogo.produto.entities;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
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
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PRODUTO", schema = "CATALOGO")
@EntityListeners(AuditingEntityListener.class)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "instituicao_id")
    private Long instituicao;

    @Column(name = "evento_id")
    private Long evento;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 50)
    private String especificacao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorVendaUnitario;

    @Column
    private Integer quantidadeMinima;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "sub_categoria_id")
    private SubCategoria subCategoria;

    @Builder.Default
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estoque> estoques = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

}
