package br.com.rts.eventmanager.catalogo.categoria.entities;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "CATEGORIA", schema = "CATALOGO")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instituicao_id", nullable = false, updatable = false)
    private Long instituicao;

    @Column(nullable = false, length = 100)
    private String nome;

    private Boolean ativo;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "categoria",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SubCategoria> subCategorias = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
