package br.com.rts.eventmanager.seguranca.configurations;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfilusuario.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.perfilusuario.services.PerfilUsuarioService;
import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProvisioningService {

    private final UsuarioService usuarioService;
    private final PerfilUsuarioService perfilUsuarioService;

    @Transactional
    public Set<GrantedAuthority> provisionAndMapAuthorities(@NotNull String email,
                                                            @NotNull String nome,
                                                            @NotNull String urlFoto) {

        Usuario usuario = usuarioService.findByEmail(email)
                .orElseGet(() -> {
                    String finalNome = nome != null ? nome : email.split("@")[0];
                    Usuario novoUsuario = usuarioService.createUsuario(email, finalNome, urlFoto);
                    novoUsuario.getPerfilUsuarios().addAll(perfilUsuarioService.getOrCreateInitialPerfil(novoUsuario));
                    return novoUsuario;
                });

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (usuario.isAtivo() && usuario.getPerfilUsuarios() != null) {
            for (PerfilUsuario perfilUsuario : usuario.getPerfilUsuarios()) {
                Perfil perfil = perfilUsuario.getPerfil();
                if (perfil != null) {
                    String roleName = "ROLE_" + perfil.getNome().toUpperCase().replace(" ", "_");
                    authorities.add(new SimpleGrantedAuthority(roleName));

                    if ("MASTER".equalsIgnoreCase(perfil.getNome()) || "ROLE_MASTER".equalsIgnoreCase(perfil.getNome())) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_MASTER"));
                    }

                    if (perfil.getPermissoes() != null) {
                        for (Permissao perm : perfil.getPermissoes()) {
                            authorities.add(new SimpleGrantedAuthority(perm.getAuthority()));
                        }
                    }
                }
            }
        }
        return authorities;
    }
}
