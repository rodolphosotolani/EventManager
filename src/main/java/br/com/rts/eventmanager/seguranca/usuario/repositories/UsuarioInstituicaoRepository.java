package br.com.rts.eventmanager.seguranca.usuario.repositories;

import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioInstituicaoRepository extends JpaRepository<UsuarioInstituicao, Long> {

    boolean existsByUsuarioIdAndInstituicao(Long usuarioId, Long instituicaoId);

    List<UsuarioInstituicao> findAllByUsuarioId(Long usuarioId);

    List<UsuarioInstituicao> findAllByInstituicao(Long instituicaoId);
}
