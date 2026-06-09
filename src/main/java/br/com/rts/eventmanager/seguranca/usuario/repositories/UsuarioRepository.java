package br.com.rts.eventmanager.seguranca.usuario.repositories;

import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "LEFT JOIN FETCH u.perfilUsuarios pu " +
           "LEFT JOIN FETCH pu.perfil p " +
           "LEFT JOIN FETCH p.permissoes " +
           "LEFT JOIN FETCH u.usuarioInstituicaos " +
           "WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);
}
