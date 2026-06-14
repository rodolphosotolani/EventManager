package br.com.rts.eventmanager.seguranca.usuario.services;

import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Usuario create(Usuario request);

    Usuario createUsuario(String email, String nome, String urlFoto);

    Usuario getUsuarioById(Long id);

    List<Usuario> list();

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findFetchAllByEmail(String email);
}
