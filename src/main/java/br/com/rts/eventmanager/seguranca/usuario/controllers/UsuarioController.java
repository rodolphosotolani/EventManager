package br.com.rts.eventmanager.seguranca.usuario.controllers;

import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_LISTAR')")
    public String list(Model model) {
        model.addAttribute("usuarios", service.list());
        model.addAttribute("pageTitle", "Usuários");
        return "usuario/list";
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public String me(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof OAuth2User oauth2User) {

            String email = oauth2User.getAttribute("email");
            Usuario usuario = service.findFetchAllByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));

            model.addAttribute("usuario", usuario);

        } else {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Meus Dados");

        return "usuario/me";
    }

}
