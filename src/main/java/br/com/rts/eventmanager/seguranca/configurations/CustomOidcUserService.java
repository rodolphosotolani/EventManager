package br.com.rts.eventmanager.seguranca.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserProvisioningService provisioningService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String nome = oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getGivenName();
        String urlFoto = oidcUser.getPicture();

        Set<GrantedAuthority> authorities = new HashSet<>(
                provisioningService.provisionAndMapAuthorities(email, nome, urlFoto)
        );
        authorities.addAll(oidcUser.getAuthorities());

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
