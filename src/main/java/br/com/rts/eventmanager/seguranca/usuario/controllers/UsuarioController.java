package br.com.rts.eventmanager.seguranca.usuario.controllers;

import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.gestao.InstituicaoDTO;
import br.com.rts.eventmanager.seguranca.PerfilDTO;
import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.seguranca.usuario.repositories.UsuarioRepository;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final PerfilService perfilService;
    private final UsuarioRepository usuarioRepository;
    private final GestaoFacade gestaoFacade;

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
        List<InstituicaoDTO> todasInstituicoes = gestaoFacade.findAllInstituicao();
        
        Map<Long, String> instituicaoNomes = todasInstituicoes.stream()
                .collect(Collectors.toMap(InstituicaoDTO::id, InstituicaoDTO::nome));

        Set<Long> activeInstituicaoIds = usuario.getUsuarioInstituicaos().stream()
                .map(UsuarioInstituicao::getInstituicao)
                .collect(Collectors.toSet());

        List<InstituicaoDTO> disponiveis = todasInstituicoes.stream()
                .filter(inst -> !activeInstituicaoIds.contains(inst.id()))
                .collect(Collectors.toList());

        model.addAttribute("usuario", usuario);
        model.addAttribute("instituicoesDisponiveis", disponiveis);
        model.addAttribute("instituicaoNomes", instituicaoNomes);
        model.addAttribute("pageTitle", "Associar Usuário a Instituições");
        return "usuario/edit";
    }

    @GetMapping("/edit/perfis")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public List<PerfilDTO> getPerfisForInstituicao(@RequestParam("instituicaoId") Long instituicaoId) {
        List<Perfil> perfis = perfilService.listByInstituicao(instituicaoId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isMaster = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MASTER".equals(a.getAuthority()));

        if (!isMaster) {
            perfis = perfis.stream()
                    .filter(p -> !"MASTER".equalsIgnoreCase(p.getNome()) && !"ROLE_MASTER".equalsIgnoreCase(p.getNome()))
                    .collect(Collectors.toList());
        }

        return perfis.stream()
                .map(p -> new PerfilDTO(p.getId(), p.getNome()))
                .collect(Collectors.toList());
    }

    @PostMapping("/edit/{id}/vincular")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public String vincularInstituicao(@PathVariable Long id,
                                      @RequestParam("instituicaoId") Long instituicaoId,
                                      @RequestParam(value = "perfilIds", required = false) List<Long> perfilIds,
                                      final RedirectAttributes redirectAttributes) {
        if (perfilIds == null || perfilIds.isEmpty()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Selecione ao menos um perfil para vincular!");
            return "redirect:/usuarios/edit/" + id;
        }

        service.linkToInstituicao(id, instituicaoId, true);

        for (Long perfilId : perfilIds) {
            service.assignPerfilToInstituicao(id, perfilId, instituicaoId);
        }

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Instituição e perfis vinculados com sucesso!");
        return "redirect:/usuarios/edit/" + id;
    }

    @PostMapping("/edit/{id}/desvincular/{instituicaoId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public String desvincularInstituicao(@PathVariable Long id,
                                         @PathVariable Long instituicaoId,
                                         final RedirectAttributes redirectAttributes) {
        service.linkToInstituicao(id, instituicaoId, false);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Associação inativada com sucesso!");
        return "redirect:/usuarios/edit/" + id;
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public String me(Model model) {
        org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
                authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
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
