package br.com.rts.eventmanager.catalogo.estoque.controllers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
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

import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService service;
    private final ProdutoService produtoService;

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;

    private final SubCategoriaService subCategoriaService;
    private final SubCategoriaMapper subCategoriaMapper;

    //    @ModelAttribute
//    public void prepareContext(final Model model) {
//        model.addAttribute("categoriaIdValues", categoriaMapper.entityToDTO(categoriaService.findAll()));
//
//        if (!model.containsAttribute("subCategoriaIdValues")) {
//            model.addAttribute("subCategoriaIdValues", Collections.emptyList());
//        }
//
//        if (!model.containsAttribute("produtoIdValues")) {
//            model.addAttribute("produtoIdValues", Collections.emptyList());
//        }
//    }


    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUES_LISTAR')")
    public String list(HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (activeEvId != null) {
            model.addAttribute("estoques", service.findAllByInstituicaoAndEvento(tenantId, activeEvId, Pageable.ofSize(100)).getContent());
        } else {
            model.addAttribute("estoques", java.util.Collections.emptyList());
        }

        model.addAttribute("pageTitle", "Estoque");
        return "estoque/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUES_CADASTRAR')")
    public String add(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Nenhum evento ativo cadastrado!");
            return "redirect:/estoques";
        }

        Estoque estoque = new Estoque();
        model.addAttribute("estoque", estoque);
        model.addAttribute("produtos", produtoService.findAllByInstituicaoAndEvento(tenantId, activeEvId, Pageable.ofSize(100)).getContent());
        model.addAttribute("pageTitle", "Registrar Entrada");
        return "estoque/add";
    }


    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUES_CADASTRAR')")
    public String add(HttpSession session,
                      @ModelAttribute("estoque") @Valid final Estoque estoque,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Nenhum evento ativo cadastrado!");
            return "redirect:/estoques";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("produtos", produtoService.findAllByInstituicaoAndEvento(tenantId, activeEvId, Pageable.ofSize(100)).getContent());
            model.addAttribute("pageTitle", "Registrar Entrada");
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar o estoque pois o formulário esta com erro.");
            log.error("Não é possível cadastrar o estoque pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "estoque/add";
        }

        estoque.setUuid(UUID.randomUUID());
        estoque.setEvento(activeEvId);
        estoque.setDateCreated(java.time.LocalDateTime.now());
        estoque.setLastUpdated(java.time.LocalDateTime.now());

        service.create(estoque, tenantId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estoque.create.success"));
        return "redirect:/estoques";
    }


    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUES_EDITAR')")
    public String edit(@PathVariable Long id,
                       HttpSession session,
                       Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/estoques";
        }

        Estoque estoque = service.findByIdAndInstituicaoAndEvento(id, tenantId, activeEvId);
        if (estoque == null) {
            throw new IllegalArgumentException("Registro de estoque não encontrado!");
        }

        model.addAttribute("estoque", estoque);
        model.addAttribute("pageTitle", "Editar Entrada");
        return "estoque/edit";
    }


    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUES_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       HttpSession session,
                       @ModelAttribute("estoque") @Valid final Estoque estoque,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/estoques";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Entrada");
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar o estoque pois o formulário esta com erro.");
            log.error("Não é possível editar o estoque pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "estoque/edit";
        }

        Estoque existing = service.findByIdAndInstituicaoAndEvento(id, tenantId, activeEvId);
        if (existing == null) {
            throw new IllegalArgumentException("Registro de estoque não encontrado!");
        }

        existing.setQuantidadeAtual(estoque.getQuantidadeAtual());
        existing.setValorCompraUnitario(estoque.getValorCompraUnitario());
        existing.setEvento(activeEvId);
        existing.setLastUpdated(java.time.LocalDateTime.now());

        service.updateEstoque(id, existing, tenantId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estoque.update.success"));
        return "redirect:/estoques";
    }


    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUES_DELETAR')")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         final RedirectAttributes redirectAttributes) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

            try {
                service.delete(id, tenantId, activeEvId);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("estoque.delete.success"));
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não foi possível excluir o estoque.");
            }
        return "redirect:/estoques";
    }

    @GetMapping("/filtros-fragment")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUES_LISTAR', 'ESTOQUES_CADASTRAR', 'ESTOQUES_EDITAR')")
    public String getFiltrosFragment(
            @RequestParam(required = false) final Long categoriaId,
            @RequestParam(required = false) final Long subCategoriaId,
            final Model model) {

        Estoque estoque = new Estoque();
//        estoque.setCategoria(categoriaId);
//        estoque.setSubCategoria(subCategoriaId);

        model.addAttribute("estoque", estoque);

//        model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(categoriaId)));
//
//        model.addAttribute("produtoIdValues", produtoMapper.entityToDTO(produtoService.findAllByCategoriaIdAndSubCategoriaId(categoriaId, subCategoriaId)));

        return "estoque/fragments/filtros :: oobFiltros";
    }
}