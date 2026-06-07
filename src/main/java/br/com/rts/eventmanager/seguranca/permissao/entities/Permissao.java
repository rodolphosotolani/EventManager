package br.com.rts.eventmanager.seguranca.permissao.entities;

import br.com.rts.eventmanager.seguranca.permissao.enumerators.AcaoPermissaoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PERMISSAO", schema = "SEGURANCA")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tela;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AcaoPermissaoEnum acao;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;



    public String getAuthority() {
        return this.tela.toUpperCase() + "_" + this.acao.name();
    }
}
