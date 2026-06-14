package br.com.rts.eventmanager.seguranca.usuario.entities;

import br.com.rts.eventmanager.seguranca.perfilusuario.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.entities.UsuarioInstituicao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "USUARIO", schema = "SEGURANCA")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "url_foto", length = 500)
    private String urlFoto;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("ativo = true")
    private Set<UsuarioInstituicao> usuarioInstituicaos = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PerfilUsuario> perfilUsuarios = new LinkedHashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

}
