package br.com.rts.eventmanager.seguranca.usuario.services;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;

import java.util.List;
import java.util.Set;

public interface PerfilUsuarioService {

    Set<PerfilUsuario> getOrCreateInitialPerfil(Usuario usuario);

    boolean existsByUsuarioIdAndPerfilIdAndInstituicao(Long usuarioId, Long perfilId, Long instituicaoId);

    List<PerfilUsuario> findAllByUsuarioIdAndInstituicao(Long id, Long instituicaoId);

    PerfilUsuario create(Perfil perfil, Usuario usuario, Long instituicaoId);
}
