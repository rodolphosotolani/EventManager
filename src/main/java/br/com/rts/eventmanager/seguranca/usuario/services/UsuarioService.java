package br.com.rts.eventmanager.seguranca.usuario.services;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;

import java.util.List;

public interface UsuarioService {

    Usuario create(Usuario request);

    Usuario get(Long id);

    List<Usuario> list();

    UsuarioInstituicao linkToInstituicao(Long usuarioId, Long instituicaoId);

    PerfilUsuario assignPerfilToEvent(Long usuarioId, Long perfilId, Long eventoId);

    List<Perfil> listPerfisByEvent(Long usuarioId, Long eventoId);
}
