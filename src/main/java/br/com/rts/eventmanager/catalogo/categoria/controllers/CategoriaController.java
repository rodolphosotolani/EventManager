package br.com.rts.eventmanager.catalogo.categoria.controllers;

import br.com.rts.eventmanager.catalogo.categoria.dtos.CategoriaDTO;
import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.utils.ReferencedException;
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


@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;
    private final CategoriaMapper mapper;

    private final SubCategoriaService subCategoriaService;
    private final SubCategoriaMapper subCategoriaMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIA_LISTAGEM')")
    public String list(final Model model) {

        model.addAttribute("categorias", mapper.entityToDTO(service.findAll()));

        return "categoria/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIA_CADASTRAR')")
    public String add(@ModelAttribute("categoria") final CategoriaDTO categoriaDTO) {

        return "categoria/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIA_CADASTRAR')")
    public String add(@ModelAttribute("categoria") @Valid final CategoriaDTO categoriaDTO,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar a categoria pois o formulário esta com erro.");
            log.error("Não é possível cadastrar a categoria pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "categoria/add";
        }
        Long id = service.create(mapper.dtoToEntity(categoriaDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("categoria.create.success"));

        return "redirect:/categorias/edit/" + id;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIA_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       final Model model) {

        model.addAttribute("categoria", mapper.entityToDTO(service.findById(id)));

        model.addAttribute("subCategorias", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(id)));

        return "categoria/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIA_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       @ModelAttribute("categoria") @Valid final CategoriaDTO categoriaDTO,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar a categoria pois o formulário esta com erro.");
            log.error("Não é possível editar a categoria pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "categoria/edit";
        }
        service.update(id, mapper.dtoToEntity(categoriaDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("categoria.update.success"));

        return "redirect:/categorias";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CATEGORIA_DELETAR')")
    public String delete(@PathVariable(name = "id") final Long id,
                         final RedirectAttributes redirectAttributes) {

        try {

            service.delete(id);

            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("categoria.delete.success"));

        } catch (final ReferencedException referencedException) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
                    referencedException.getKey(), referencedException.getParams().toArray()));
        }

        return "redirect:/categorias";
    }

}
