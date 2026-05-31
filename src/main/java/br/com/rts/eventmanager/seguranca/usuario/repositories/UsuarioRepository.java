package br.com.rts.eventmanager.seguranca.usuario.repositories;

import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
