package br.com.rts.eventmanager.gestao.evento.controllers;

import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import br.com.rts.eventmanager.gestao.evento.services.EventoService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String list(HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("eventosList", service.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
        model.addAttribute("pageTitle", "Eventos");
        return "evento/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String add(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("pageTitle", "Novo Evento");
        return "evento/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String add(HttpSession session,
                      @ModelAttribute("evento") @Valid final Evento evento,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Novo Evento");
            return "evento/add";
        }

        evento.setAtivo(true);
        evento.setDateCreated(java.time.LocalDateTime.now());
        evento.setLastUpdated(java.time.LocalDateTime.now());
        
        service.create(tenantId, evento);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Evento cadastrado com sucesso!");
        return "redirect:/eventos";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String edit(@PathVariable Long id, HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        Evento evento = service.findByIdAndInstituicao(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado!"));
        
        model.addAttribute("evento", evento);
        model.addAttribute("pageTitle", "Editar Evento");
        return "evento/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String edit(@PathVariable Long id,
                       HttpSession session,
                      @ModelAttribute("evento") @Valid final Evento evento,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Evento");
            return "evento/edit";
        }

        Evento existing = service.findByIdAndInstituicao(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado!"));

        existing.setNome(evento.getNome());
        existing.setAtivo(evento.getAtivo() != null ? evento.getAtivo() : true);
        existing.setLastUpdated(java.time.LocalDateTime.now());

        service.update(id, tenantId, existing);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Evento atualizado com sucesso!");
        return "redirect:/eventos";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String delete(@PathVariable Long id, HttpSession session, final RedirectAttributes redirectAttributes) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId != null) {
            try {
                service.delete(id, tenantId);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Evento excluído com sucesso!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não foi possível excluir o evento.");
            }
        }
        return "redirect:/eventos";
    }
}
