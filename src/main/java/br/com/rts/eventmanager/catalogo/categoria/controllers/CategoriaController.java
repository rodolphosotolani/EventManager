package br.com.rts.eventmanager.catalogo.categoria.controllers;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;
    private final SubCategoriaService subCategoriaService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIAS_LISTAR')")
    public String list(HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("categorias", service.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
        model.addAttribute("pageTitle", "Categorias");
        return "categoria/list";
    }


    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIAS_CADASTRAR')")
    public String add(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("pageTitle", "Nova Categoria");
        return "categoria/add";
    }


    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIAS_CADASTRAR')")
    public String add(HttpSession session,
                      @ModelAttribute("categoria") @Valid final Categoria categoria,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Nova Categoria");
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar a categoria pois o formulário esta com erro.");
            log.error("Não é possível cadastrar a categoria pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "categoria/add";
        }

        categoria.setAtivo(true);
        categoria.setDateCreated(java.time.LocalDateTime.now());
        categoria.setLastUpdated(java.time.LocalDateTime.now());

        service.create(tenantId, categoria);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("categoria.create.success"));
        return "redirect:/categorias";
    }


    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIAS_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       HttpSession session,
                       Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        Categoria categoria = service.findByIdAndInstituicao(id, tenantId);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não encontrada!");
        }

        model.addAttribute("categoria", categoria);
        model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, id, Pageable.ofSize(100)).getContent());
        model.addAttribute("pageTitle", "Editar Categoria");
        return "categoria/edit";
    }


    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIAS_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       @ModelAttribute("categoria") @Valid final Categoria categoria,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       HttpSession session,
                       Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Categoria");
            model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, id, Pageable.ofSize(100)).getContent());
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar a categoria pois o formulário esta com erro.");
            log.error("Não é possível editar a categoria pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "categoria/edit";
        }

        service.update(tenantId, id, categoria);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Categoria atualizada com sucesso!");
        return "redirect:/categorias";
    }


    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIAS_DELETAR')")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         final RedirectAttributes redirectAttributes) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId != null) {
            try {
                service.delete(tenantId, id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Categoria excluída com sucesso!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não foi possível excluir a categoria pois ela está vinculada a produtos/subcategorias.");
            }
        }
        return "redirect:/categorias";
    }
}