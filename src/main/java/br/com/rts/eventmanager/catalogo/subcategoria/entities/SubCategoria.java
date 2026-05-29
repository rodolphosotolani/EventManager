package br.com.rts.eventmanager.catalogo.subcategoria.entities;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sub_categoria", schema = "catalogo")
public class SubCategoria {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private Long instituicao_id;

    @Column(length = 100)
    private String nome;

    @ManyToOne()
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
