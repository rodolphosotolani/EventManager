//package br.com.rts.eventmanager.catalogo.produto.controllers;
//
//import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
//import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
//import br.com.rts.eventmanager.catalogo.produto.dtos.ProdutoDTO;
//import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
//import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
//import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
//import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
//import br.com.rts.eventmanager.utils.ReferencedException;
//import br.com.rts.eventmanager.utils.WebUtils;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import static java.util.Collections.emptyMap;
//
//@Log4j2
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/produtos")
//public class ProdutoController {
//
//    private final ProdutoService service;
//    private final ProdutoMapper mapper;
//
//    private final CategoriaService categoriaService;
//    private final CategoriaMapper categoriaMapper;
//
//    private final SubCategoriaService subCategoriaService;
//    private final SubCategoriaMapper subCategoriaMapper;
//
//    @ModelAttribute
//    public void prepareContext(final Model model) {
//        model.addAttribute("categoriaIdValues", categoriaMapper.entityToDTO(categoriaService.findAll()));
//    }
//
//    @GetMapping("/subcategorias-fragment")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_LISTAGEM', 'PRODUTO_CADASTRAR', 'PRODUTO_EDITAR')")
//    public String getSubCategoriasFragment(@RequestParam(name = "categoriaId", required = false) final Long categoriaId,
//                                           final Model model) {
//
//        ProdutoDTO produto = new ProdutoDTO();
//        produto.setCategoriaId(categoriaId);
//
//        model.addAttribute("produto", produto);
//
//        if (categoriaId != null) {
//            model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(categoriaId)));
//        } else {
//            model.addAttribute("subCategoriaIdValues", emptyMap());
//        }
//
//        return "produto/fragments/subCategoriaOptions :: select";
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_LISTAGEM')")
//    public String list(final Model model) {
//
//        model.addAttribute("produtos", mapper.entityToDTO(service.findAll()));
//
//        return "produto/list";
//    }
//
//    @GetMapping("/add")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_CADASTRAR')")
//    public String add(@ModelAttribute("produto") final ProdutoDTO produtoDTO) {
//        return "produto/add";
//    }
//
//    @PostMapping("/add")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_CADASTRAR')")
//    public String add(@ModelAttribute("produto") @Valid final ProdutoDTO produtoDTO,
//                      final BindingResult bindingResult,
//                      final Model model,
//                      final RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar o produto pois o formulário esta com erro.");
//            log.error("Não é possível cadastrar o produto pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
//            if (produtoDTO.getCategoriaId() != null) {
//                model.addAttribute("subCategoriaValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(produtoDTO.getCategoriaId())));
//            } else {
//                model.addAttribute("subCategoriaValues", emptyMap());
//            }
//            return "produto/add";
//        }
//
//        service.create(mapper.dtoToEntity(produtoDTO));
//
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("produto.create.success"));
//
//        return "redirect:/produtos";
//    }
//
//    @GetMapping("/edit/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_EDITAR')")
//    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
//
//        ProdutoDTO produto = mapper.entityToDTO(service.findById(id));
//
//        model.addAttribute("produto", produto);
//
//        return "produto/edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_EDITAR')")
//    public String edit(@PathVariable(name = "id") final Long id,
//                       @ModelAttribute("produto") @Valid final ProdutoDTO produtoDTO,
//                       final BindingResult bindingResult,
//                       final Model model,
//                       final RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar o produto pois o formulário esta com erro.");
//            log.error("Não é possível editar o produto pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
//            if (produtoDTO.getCategoriaId() != null) {
//                model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(produtoDTO.getCategoriaId())));
//            } else {
//                model.addAttribute("subCategoriaIdValues", emptyMap());
//            }
//            return "produto/edit";
//        }
//
//        service.update(id, mapper.dtoToEntity(produtoDTO));
//
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("produto.update.success"));
//
//        return "redirect:/produtos";
//    }
//
//    @PostMapping("/delete/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_DELETAR')")
//    public String delete(@PathVariable(name = "id") final Long id,
//                         final RedirectAttributes redirectAttributes) {
//        try {
//
//            service.delete(id);
//
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("produto.delete.success"));
//
//        } catch (final ReferencedException referencedException) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
//                    referencedException.getKey(), referencedException.getParams().toArray()));
//        }
//
//        return "redirect:/produtos";
//    }
//
//}
