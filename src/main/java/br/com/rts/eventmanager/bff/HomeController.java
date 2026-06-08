package br.com.rts.eventmanager.bff;

import br.com.rts.eventmanager.gestao.InstituicaoDTO;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final GestaoFacade instituicaoService;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) Long instituicaoId, Model model) {
        Page<InstituicaoDTO> page = instituicaoService.findAllInstituicao(Pageable.ofSize(100));
        model.addAttribute("instituicoes", page.getContent());

        InstituicaoDTO tenant = null;
        if (instituicaoId != null) {
            tenant = instituicaoService.findInstituicaoById(instituicaoId).orElse(null);
        } else if (!page.isEmpty()) {
            tenant = page.getContent().get(0);
        }
        
        model.addAttribute("tenant", tenant);
        populateTenantColors(tenant, model);
        model.addAttribute("pageTitle", "Dashboard");

        return "dashboard/index";
    }

    @GetMapping("/dashboard/fragment")
    public String dashboardFragment(@RequestParam(required = false) Long instituicaoId, Model model) {
        Page<InstituicaoDTO> page = instituicaoService.findAllInstituicao(Pageable.ofSize(100));
        
        InstituicaoDTO tenant = null;
        if (instituicaoId != null) {
            tenant = instituicaoService.findInstituicaoById(instituicaoId)
                    .orElse(null);
        } else if (!page.isEmpty()) {
            tenant = page.getContent().get(0);
        }

        model.addAttribute("tenant", tenant);
        populateTenantColors(tenant, model);

        return "dashboard/sales_fragment :: sales-panel";
    }

    private void populateTenantColors(InstituicaoDTO tenant, Model model) {
        if (tenant != null) {
            if (tenant.id() == 1L) {
                // Stitch Base: orange theme
                model.addAttribute("tenantColorPrimary", "#e65100");
                model.addAttribute("tenantColorSecondary", "#bf360c");
                model.addAttribute("tenantColorPrimaryContainer", "#cd4700");
            } else {
                // Indigo/Navy dynamic theme for other institutions
                model.addAttribute("tenantColorPrimary", "#1e3a8a");
                model.addAttribute("tenantColorSecondary", "#3b82f6");
                model.addAttribute("tenantColorPrimaryContainer", "#2563eb");
            }
        }
    }

}
