package br.com.rts.eventmanager.seguranca.perfil.controllers;

import br.com.rts.eventmanager.seguranca.PerfilDTO;
import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.permissao.services.PermissaoService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/perfis")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService service;
    private final PermissaoService permissaoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PERFIS_LISTAR')")
    public String list(HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("perfis", service.listByInstituicao(tenantId));
        model.addAttribute("pageTitle", "Perfis de Acesso");
        return "perfil/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PERFIS_CADASTRAR')")
    public String add(Model model) {
        model.addAttribute("perfil", new Perfil());
        model.addAttribute("permissoes", permissaoService.findAllByTela(null, Pageable.ofSize(200)).getContent());
        model.addAttribute("pageTitle", "Novo Perfil");
        return "perfil/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PERFIS_CADASTRAR')")
    public String add(HttpSession session,
                      @ModelAttribute("perfil") @Valid final Perfil perfil,
                      final BindingResult bindingResult,
                      @RequestParam(value = "permissaoIds", required = false) List<Long> permissaoIds,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("permissoes", permissaoService.findAllByTela(null, Pageable.ofSize(200)).getContent());
            model.addAttribute("pageTitle", "Novo Perfil");
            return "perfil/add";
        }

        perfil.setInstituicao(tenantId);
        perfil.setDateCreated(java.time.LocalDateTime.now());
        perfil.setLastUpdated(java.time.LocalDateTime.now());

        if (permissaoIds == null) {
            permissaoIds = new ArrayList<>();
        }

        service.create(perfil, permissaoIds);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Perfil cadastrado com sucesso!");
        return "redirect:/perfis";
    }

    @GetMapping("/edit/perfis")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public List<PerfilDTO> getPerfisForInstituicao(@RequestParam("instituicaoId") Long instituicaoId) {
        List<Perfil> perfis = service.listByInstituicao(instituicaoId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isMaster = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MASTER".equals(a.getAuthority()));

        if (!isMaster) {
            perfis = perfis.stream()
                    .filter(p -> !"MASTER".equalsIgnoreCase(p.getNome()) && !"ROLE_MASTER".equalsIgnoreCase(p.getNome()))
                    .toList();
        }

        return perfis.stream()
                .map(p -> new PerfilDTO(p.getId(), p.getNome()))
                .collect(Collectors.toList());
    }


}
