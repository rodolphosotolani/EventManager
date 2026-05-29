package br.com.rts.eventmanager.catalogo.movimentacao.controllers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.movimentacao.dtos.MovimentacaoDTO;
import br.com.rts.eventmanager.catalogo.movimentacao.mappers.MovimentacaoMapper;
import br.com.rts.eventmanager.catalogo.movimentacao.services.MovimentacaoService;
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
@RequestMapping("/movimentacaos")
public class MovimentacaoController {

    private final MovimentacaoService service;
    private final MovimentacaoMapper mapper;

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
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'MOVIMENTACAO_LISTAGEM')")
    public String list(final Model model) {
        model.addAttribute("movimentacaos", mapper.entityToDTO(service.findAll()));
        return "movimentacao/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'MOVIMENTACAO_CADASTRAR')")
    public String add(@ModelAttribute("movimentacao") final MovimentacaoDTO movimentacaoDTO) {
        return "movimentacao/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'MOVIMENTACAO_CADASTRAR')")
    public String add(@ModelAttribute("movimentacao") @Valid final MovimentacaoDTO movimentacaoDTO,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar o movimentacao pois o formulário esta com erro.");
            log.error("Não é possível cadastrar o movimentacao pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "movimentacao/add";
        }

        service.create(mapper.dtoToEntity(movimentacaoDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("movimentacao.create.success"));

        return "redirect:/movimentacaos";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'MOVIMENTACAO_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       final Model model) {

        model.addAttribute("movimentacao", mapper.entityToDTO(service.get(id)));

        return "movimentacao/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'MOVIMENTACAO_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       @ModelAttribute("movimentacao") @Valid final MovimentacaoDTO movimentacaoDTO,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar o movimentacao pois o formulário esta com erro.");
            log.error("Não é possível editar o movimentacao pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "movimentacao/edit";
        }

        service.update(id, mapper.dtoToEntity(movimentacaoDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("movimentacao.update.success"));

        return "redirect:/movimentacaos";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'MOVIMENTACAO_DELETAR')")
    public String delete(@PathVariable(name = "id") final Long id,
                         final RedirectAttributes redirectAttributes) {

        service.delete(id);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("movimentacao.delete.success"));

        return "redirect:/movimentacaos";
    }

    @GetMapping("/filtros-fragment")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'MOVIMENTACAO_LISTAGEM', 'MOVIMENTACAO_CADASTRAR', 'MOVIMENTACAO_EDITAR')")
    public String getFiltrosFragment(
            @RequestParam(required = false) final Long categoriaId,
            @RequestParam(required = false) final Long subCategoriaId,
            final Model model) {

        MovimentacaoDTO movimentacao = new MovimentacaoDTO();
        movimentacao.setCategoriaId(categoriaId);
        movimentacao.setSubCategoriaId(subCategoriaId);

        model.addAttribute("movimentacao", movimentacao);

        model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(categoriaId)));

        model.addAttribute("produtoIdValues", produtoMapper.entityToDTO(produtoService.findAllByCategoriaIdAndSubCategoriaId(categoriaId, subCategoriaId)));

        return "movimentacao/fragments/filtros :: oobFiltros";
    }

}
