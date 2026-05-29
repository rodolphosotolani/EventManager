package br.com.rts.eventmanager.catalogo.estoque.controllers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.estoque.dtos.EstoqueDTO;
import br.com.rts.eventmanager.catalogo.estoque.mappers.EstoqueMapper;
import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/estoques")
public class EstoqueController {

    private final EstoqueService service;
    private final EstoqueMapper mapper;

    private final ProdutoService produtoService;
    private final ProdutoMapper produtoMapper;

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;

    private final SubCategoriaService subCategoriaService;
    private final SubCategoriaMapper subCategoriaMapper;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("categoriaIdValues", categoriaMapper.entityToDTO(categoriaService.findAll()));

        if (!model.containsAttribute("subCategoriaIdValues")) {
            model.addAttribute("subCategoriaIdValues", Collections.emptyList());
        }

        if (!model.containsAttribute("produtoIdValues")) {
            model.addAttribute("produtoIdValues", Collections.emptyList());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUE_LISTAGEM')")
    public String list(final Model model) {
        model.addAttribute("estoques", mapper.entityToDTO(service.findAll()));
        return "estoque/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUE_CADASTRAR')")
    public String add(@ModelAttribute("estoque") final EstoqueDTO estoqueDTO) {
        return "estoque/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUE_CADASTRAR')")
    public String add(@ModelAttribute("estoque") @Valid final EstoqueDTO estoqueDTO,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar o estoque pois o formulário esta com erro.");
            log.error("Não é possível cadastrar o estoque pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "estoque/add";
        }

        service.create(mapper.dtoToEntity(estoqueDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estoque.create.success"));

        return "redirect:/estoques";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUE_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       final Model model) {

        model.addAttribute("estoque", mapper.entityToDTO(service.get(id)));

        return "estoque/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUE_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       @ModelAttribute("estoque") @Valid final EstoqueDTO estoqueDTO,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar o estoque pois o formulário esta com erro.");
            log.error("Não é possível editar o estoque pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "estoque/edit";
        }

        service.update(id, mapper.dtoToEntity(estoqueDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estoque.update.success"));

        return "redirect:/estoques";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUE_DELETAR')")
    public String delete(@PathVariable(name = "id") final Long id,
                         final RedirectAttributes redirectAttributes) {

        service.delete(id);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("estoque.delete.success"));

        return "redirect:/estoques";
    }

    @GetMapping("/filtros-fragment")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ESTOQUE_LISTAGEM', 'ESTOQUE_CADASTRAR', 'ESTOQUE_EDITAR')")
    public String getFiltrosFragment(
            @RequestParam(required = false) final Long categoriaId,
            @RequestParam(required = false) final Long subCategoriaId,
            final Model model) {

        EstoqueDTO estoque = new EstoqueDTO();
        estoque.setCategoriaId(categoriaId);
        estoque.setSubCategoriaId(subCategoriaId);

        model.addAttribute("estoque", estoque);

        model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(categoriaId)));

        model.addAttribute("produtoIdValues", produtoMapper.entityToDTO(produtoService.findAllByCategoriaIdAndSubCategoriaId(categoriaId, subCategoriaId)));

        return "estoque/fragments/filtros :: oobFiltros";
    }

}
