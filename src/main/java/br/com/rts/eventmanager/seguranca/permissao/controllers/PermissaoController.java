package br.com.rts.eventmanager.seguranca.permissao.controllers;

import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.permissao.services.PermissaoService;
import br.com.rts.eventmanager.utils.WebUtils;
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
@RequestMapping("/permissoes")
@RequiredArgsConstructor
public class PermissaoController {

    private final PermissaoService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String list(Model model) {
        model.addAttribute("permissoes", service.findAllByTela(null, Pageable.ofSize(200)).getContent());
        model.addAttribute("pageTitle", "Permissões de Acesso");
        return "permissao/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String add(Model model) {
        model.addAttribute("permissao", new Permissao());
        model.addAttribute("pageTitle", "Nova Permissão");
        return "permissao/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String add(@ModelAttribute("permissao") @Valid final Permissao permissao,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Nova Permissão");
            return "permissao/add";
        }

        permissao.setDateCreated(java.time.LocalDateTime.now());
        permissao.setLastUpdated(java.time.LocalDateTime.now());

        service.create(permissao);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Permissão cadastrada com sucesso!");
        return "redirect:/permissoes";
    }
}
