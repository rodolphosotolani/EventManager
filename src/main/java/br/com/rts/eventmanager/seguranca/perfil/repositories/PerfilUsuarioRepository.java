package br.com.rts.eventmanager.seguranca.perfil.repositories;

import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {

    List<PerfilUsuario> findAllByUsuarioIdAndEvento(Long usuarioId, Long eventoId);

    List<PerfilUsuario> findAllByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndPerfilIdAndEvento(Long usuarioId, Long perfilId, Long eventoId);
}
