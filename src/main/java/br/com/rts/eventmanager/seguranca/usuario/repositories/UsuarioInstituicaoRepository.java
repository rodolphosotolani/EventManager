package br.com.rts.eventmanager.seguranca.usuario.repositories;

import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioInstituicaoRepository extends JpaRepository<UsuarioInstituicao, Long> {

    @Query("SELECT COUNT(ui) > 0 FROM UsuarioInstituicao ui WHERE ui.usuario.id = :usuarioId AND ui.instituicao = :instituicaoId AND ui.ativo = true")
    boolean existsByUsuarioIdAndInstituicao(@Param("usuarioId") Long usuarioId, @Param("instituicaoId") Long instituicaoId);

    @Query("SELECT ui FROM UsuarioInstituicao ui WHERE ui.usuario.id = :usuarioId AND ui.ativo = true")
    List<UsuarioInstituicao> findAllByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT ui FROM UsuarioInstituicao ui WHERE ui.instituicao = :instituicaoId AND ui.ativo = true")
    List<UsuarioInstituicao> findAllByInstituicao(@Param("instituicaoId") Long instituicaoId);

    Optional<UsuarioInstituicao> findByUsuarioIdAndInstituicao(Long usuarioId, Long instituicaoId);
}
