package br.com.rts.eventmanager.seguranca.configurations;

import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TimezoneAndAccessInterceptor implements HandlerInterceptor {

    private final UsuarioService usuarioService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Skip static resources, login, logout, and the redirect target itself
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/static/") ||
                path.startsWith("/webjars/") || path.equals("/favicon.ico") ||
                path.equals("/login") || path.equals("/logout") || path.equals("/aguarde-vinculo")) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal().toString())) {

            // Check if the user has ROLE_MASTER
            boolean isMaster = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_MASTER".equals(a.getAuthority()));

            if (!isMaster) {
                // Get the logged-in user email
                String email = null;
                Object principal = authentication.getPrincipal();
                if (principal instanceof OAuth2User) {
                    email = ((OAuth2User) principal).getAttribute("email");
                }

                if (email != null) {
                    Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        
                        // Check if linked to any institution either via UsuarioInstituicao or via PerfilUsuario
                        boolean hasInstituicao = false;
                        if (usuario.getUsuarioInstituicaos() != null && !usuario.getUsuarioInstituicaos().isEmpty()) {
                            hasInstituicao = true;
                        }
                        if (usuario.getPerfilUsuarios() != null) {
                            boolean hasLinkedProfile = usuario.getPerfilUsuarios().stream()
                                    .anyMatch(pu -> pu.getInstituicao() != null);
                            if (hasLinkedProfile) {
                                hasInstituicao = true;
                            }
                        }

                        if (!hasInstituicao) {
                            response.sendRedirect("/aguarde-vinculo");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
