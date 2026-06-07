package br.com.rts.eventmanager.seguranca.perfil.repositories;

import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {

    List<PerfilUsuario> findAllByUsuarioIdAndInstituicao(Long usuarioId, Long instituicaoId);

    Set<PerfilUsuario> findAllByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndPerfilIdAndInstituicao(Long usuarioId, Long perfilId, Long instituicaoId);
}
