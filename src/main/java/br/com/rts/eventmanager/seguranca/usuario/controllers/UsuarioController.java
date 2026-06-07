package br.com.rts.eventmanager.seguranca.usuario.controllers;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.seguranca.usuario.repositories.UsuarioRepository;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final PerfilService perfilService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_LISTAR')")
    public String list(Model model) {
        model.addAttribute("usuarios", service.list());
        model.addAttribute("pageTitle", "Usuários");
        return "usuario/list";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public String edit(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");

        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        Usuario usuario = service.getUsuarioById(id);
        List<Perfil> perfis = perfilService.listByInstituicao(tenantId);
        List<Perfil> userPerfis = service.listPerfisByInstituicao(id, tenantId);
        
        Perfil currentPerfil = null;
        if (!userPerfis.isEmpty()) {
            currentPerfil = userPerfis.get(0);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", perfis);
        model.addAttribute("currentPerfilId", currentPerfil != null ? currentPerfil.getId() : null);
        model.addAttribute("pageTitle", "Associar Perfil ao Usuário");
        return "usuario/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public String edit(@PathVariable Long id,
                       @RequestParam("perfilId") Long perfilId,
                       HttpSession session,
                       final RedirectAttributes redirectAttributes) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");

        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        service.assignPerfilToInstituicao(id, perfilId, tenantId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Perfil associado com sucesso!");
        return "redirect:/usuarios";
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public String me(Model model) {
        org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));
            model.addAttribute("usuario", usuario);
        } else {
            return "redirect:/login";
        }
        
        model.addAttribute("pageTitle", "Meus Dados");
        return "usuario/me";
    }
}
