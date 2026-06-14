package br.com.rts.eventmanager.seguranca.perfilusuario.services;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfilusuario.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public interface PerfilUsuarioService {

    Set<PerfilUsuario> getOrCreateInitialPerfil(Usuario usuario);

    boolean existsByUsuarioIdAndPerfilIdAndInstituicao(Long usuarioId, Long perfilId, Long instituicaoId);

    List<PerfilUsuario> findAllByUsuarioIdAndInstituicao(@NotNull final Long usuarioId,
                                                         @NotNull final Long instituicaoId);

    PerfilUsuario create(Perfil perfil, Usuario usuario, Long instituicaoId);

    void assignPerfilToInstituicao(@NotNull final Long usuarioId,
                                   @NotNull final Long instituicaoId,
                                   @NotNull final Long perfilId);

    void assignPerfilToInstituicao(@NotNull final Long usuarioId,
                                   @NotNull final Long instituicaoId,
                                   @NotNull final List<Long> perfilIds);
}
