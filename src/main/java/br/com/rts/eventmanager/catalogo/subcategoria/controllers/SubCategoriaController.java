package br.com.rts.eventmanager.catalogo.subcategoria.controllers;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/subcategorias")
@RequiredArgsConstructor
public class SubCategoriaController {

    private final SubCategoriaService service;
    private final CategoriaService categoriaService;

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_CADASTRAR')")
    public String add(@RequestParam("categoriaId") Long categoriaId, HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        Categoria categoria = categoriaService.findByIdAndInstituicao(categoriaId, tenantId);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria inválida!");
        }

        SubCategoria subCategoria = new SubCategoria();
        subCategoria.setCategoria(categoria);

        model.addAttribute("subCategoria", subCategoria);
        model.addAttribute("categoria", categoria);
        model.addAttribute("pageTitle", "Nova Subcategoria");
        return "subcategoria/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_CADASTRAR')")
    public String add(HttpSession session,
                      @RequestParam("categoriaId") Long categoriaId,
                      @ModelAttribute("subCategoria") @Valid final SubCategoria subCategoria,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        Categoria categoria = categoriaService.findByIdAndInstituicao(categoriaId, tenantId);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria inválida!");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("pageTitle", "Nova Subcategoria");
            return "subcategoria/add";
        }

        subCategoria.setCategoria(categoria);
        subCategoria.setAtivo(true);
        subCategoria.setDateCreated(java.time.LocalDateTime.now());
        subCategoria.setLastUpdated(java.time.LocalDateTime.now());

        service.create(subCategoria, tenantId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Subcategoria cadastrada com sucesso!");
        return "redirect:/categorias/edit/" + categoriaId;
    }
}
