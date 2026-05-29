package br.com.rts.eventmanager.catalogo.subcategoria.controllers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.subcategoria.dtos.SubCategoriaDTO;
import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
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

import java.util.List;


@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/subcategorias")
public class SubCategoriaController {

    private final SubCategoriaService service;
    private final SubCategoriaMapper mapper;
    private final CategoriaMapper categoriaMapper;

    @ModelAttribute
    public void prepareContext(final Model model) {
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SUBCATEGORIA_LISTAGEM')")
    public String list(final Model model) {

        model.addAttribute("subCategorias", mapper.entityToDTO(service.findAll()));

        return "sub_categoria/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SUBCATEGORIA_CADASTRAR')")
    public String add(@ModelAttribute("subCategoria") final SubCategoriaDTO subCategoriaDTO) {

        return "sub_categoria/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SUBCATEGORIA_CADASTRAR')")
    public String add(@ModelAttribute("subCategoria") @Valid final SubCategoriaDTO subCategoriaDTO,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar a subcategoria pois o formulário esta com erro.");
            log.error("Não é possível cadastrar a subcategoria pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "sub_categoria/add";
        }
        service.create(
                mapper.dtoToEntity(subCategoriaDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("subCategoria.create.success"));

        return "redirect:/categorias/edit/" + subCategoriaDTO.getCategoriaId();
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SUBCATEGORIA_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {

        SubCategoria subCategoria = service.findById(id);
        model.addAttribute("subCategoria", mapper.entityToDTO(subCategoria));
        model.addAttribute("categoriaIdValues", List.of(categoriaMapper.entityToDTO(subCategoria.getCategoria())));

        return "sub_categoria/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SUBCATEGORIA_EDITAR')")
    public String edit(@PathVariable(name = "id") final Long id,
                       @ModelAttribute("subCategoria") @Valid final SubCategoriaDTO subCategoriaDTO,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar a subcategoria pois o formulário esta com erro.");
            log.error("Não é possível editar a subcategoria pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "sub_categoria/edit";
        }

        service.update(id, mapper.dtoToEntity(subCategoriaDTO));

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("subCategoria.update.success"));

        return "redirect:/categorias/edit/" + subCategoriaDTO.getCategoriaId();
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SUBCATEGORIA_DELETAR')")
    public String delete(@PathVariable(name = "id") final Long id,
                         final RedirectAttributes redirectAttributes) {

        SubCategoria subCategoria = service.findById(id);

        try {

            service.delete(subCategoria.getId());

            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("subCategoria.delete.success"));
        } catch (final ReferencedException referencedException) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
                    referencedException.getKey(), referencedException.getParams().toArray()));
        }

        if (subCategoria.getCategoria() != null) {
            return "redirect:/categorias/edit/" + subCategoria.getCategoria().getId();
        }

        return "redirect:/categorias";
    }

}
