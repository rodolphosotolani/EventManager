package br.com.rts.eventmanager.seguranca.usuario.services;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Usuario create(Usuario request);

    Usuario createUsuario(String email, String nome, String urlFoto);

    Usuario getUsuarioById(Long id);

    List<Usuario> list();

    UsuarioInstituicao linkToInstituicao(Long usuarioId, Long instituicaoId, boolean ativo);

    PerfilUsuario assignPerfilToInstituicao(Long usuarioId, Long perfilId, Long instituicaoId);

    List<Perfil> listPerfisByInstituicao(Long usuarioId, Long instituicaoId);

    Optional<Usuario> findByEmail(String email);
}
