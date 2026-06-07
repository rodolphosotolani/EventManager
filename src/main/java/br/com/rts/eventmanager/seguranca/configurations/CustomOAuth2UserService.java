package br.com.rts.eventmanager.seguranca.configurations;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioService usuarioService;

    private static final String PREFIX_ROLE_NAME = "ROLE_";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");
        String urlFoto = oAuth2User.getAttribute("picture");

        if (email == null) {
            log.error("Email is null. Cannot authenticate via Google OAuth2.");
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Usuario usuario = usuarioService.findByEmail(email)
                .orElseGet(() -> usuarioService.createUsuario(email, nome, urlFoto));

        // Mapear Permissoes para GrantedAuthorities
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (usuario.getPerfilUsuarios() != null) {
            for (PerfilUsuario perfilUsuario : usuario.getPerfilUsuarios()) {
                Perfil perfil = perfilUsuario.getPerfil();
                // Pela lógica Spring, roles comçam com ROLE_ se quisermos usar hasRole
                authorities.add(new SimpleGrantedAuthority(PREFIX_ROLE_NAME + perfil.getNome().toUpperCase().replace(" ", "_")));

                if (perfil.getPermissoes() != null) {
                    for (Permissao permissao : perfil.getPermissoes()) {
                        authorities.add(new SimpleGrantedAuthority(permissao.getAuthority()));
                    }
                }
            }
        }

        // Return a DefaultOAuth2User which Spring Security uses
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email" // Name attribute key
        );
    }
}
