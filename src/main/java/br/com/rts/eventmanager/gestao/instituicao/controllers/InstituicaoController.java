package br.com.rts.eventmanager.gestao.instituicao.controllers;

import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import br.com.rts.eventmanager.gestao.instituicao.services.InstituicaoService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/instituicoes")
@RequiredArgsConstructor
public class InstituicaoController {

    private final InstituicaoService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    @Transactional(readOnly = true)
    public String list(Model model) {
        model.addAttribute("instituicoesList", service.findAll(Pageable.ofSize(100)).getContent());
        model.addAttribute("pageTitle", "Instituições");
        return "instituicao/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String add(Model model) {
        model.addAttribute("instituicao", new Instituicao());
        model.addAttribute("pageTitle", "Nova Instituição");
        return "instituicao/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String add(@ModelAttribute("instituicao") @Valid final Instituicao instituicao,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        if (bindingResult.hasErrors() || instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
            if (instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
                bindingResult.rejectValue("nome", "error.instituicao", "O nome é obrigatório");
            }
            model.addAttribute("pageTitle", "Nova Instituição");
            return "instituicao/add";
        }

        instituicao.setAtivo(true);
        instituicao.setDateCreated(java.time.LocalDateTime.now());
        instituicao.setLastUpdated(java.time.LocalDateTime.now());

        service.create(instituicao);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Instituição cadastrada com sucesso!");
        return "redirect:/instituicoes";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String edit(@PathVariable Long id, Model model) {
        Instituicao instituicao = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada!"));

        model.addAttribute("instituicao", instituicao);
        model.addAttribute("pageTitle", "Editar Instituição");
        return "instituicao/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("instituicao") @Valid final Instituicao instituicao,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       Model model) {
        if (bindingResult.hasErrors() || instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
            if (instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
                bindingResult.rejectValue("nome", "error.instituicao", "O nome é obrigatório");
            }
            model.addAttribute("pageTitle", "Editar Instituição");
            return "instituicao/edit";
        }

        Instituicao existing = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada!"));

        existing.setNome(instituicao.getNome());
        existing.setAtivo(instituicao.getAtivo() != null ? instituicao.getAtivo() : true);
        existing.setLastUpdated(java.time.LocalDateTime.now());

        service.update(id, existing);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Instituição atualizada com sucesso!");
        return "redirect:/instituicoes";
    }

    @PostMapping("/toggle/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public String toggle(@PathVariable Long id, final RedirectAttributes redirectAttributes) {
        Instituicao instituicao = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada!"));

        instituicao.setAtivo(!instituicao.getAtivo());
        instituicao.setLastUpdated(java.time.LocalDateTime.now());
        service.update(id, instituicao);

        String msg = instituicao.getAtivo() ? "Instituição ativada com sucesso!" : "Instituição desativada com sucesso!";
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, msg);
        return "redirect:/instituicoes";
    }
}
