//package br.com.rts.eventmanager.catalogo.servico.controllers;
//
//import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
//import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
//import br.com.rts.eventmanager.catalogo.servico.dtos.ServicoDTO;
//import br.com.rts.eventmanager.catalogo.servico.mappers.ServicoMapper;
//import br.com.rts.eventmanager.catalogo.servico.services.ServicoService;
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
//@RequestMapping("/Servicos")
//public class ServicoController {
//
//    private final ServicoService service;
//    private final ServicoMapper mapper;
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
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'Servico_LISTAGEM', 'Servico_CADASTRAR', 'Servico_EDITAR')")
//    public String getSubCategoriasFragment(@RequestParam(name = "categoriaId", required = false) final Long categoriaId,
//                                           final Model model) {
//
//        ServicoDTO Servico = new ServicoDTO();
//        Servico.setCategoriaId(categoriaId);
//
//        model.addAttribute("Servico", Servico);
//
//        if (categoriaId != null) {
//            model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(categoriaId)));
//        } else {
//            model.addAttribute("subCategoriaIdValues", emptyMap());
//        }
//
//        return "Servico/fragments/subCategoriaOptions :: select";
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'Servico_LISTAGEM')")
//    public String list(final Model model) {
//
//        model.addAttribute("Servicos", mapper.entityToDTO(service.findAll()));
//
//        return "Servico/list";
//    }
//
//    @GetMapping("/add")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'Servico_CADASTRAR')")
//    public String add(@ModelAttribute("Servico") final ServicoDTO ServicoDTO) {
//        return "Servico/add";
//    }
//
//    @PostMapping("/add")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'Servico_CADASTRAR')")
//    public String add(@ModelAttribute("Servico") @Valid final ServicoDTO ServicoDTO,
//                      final BindingResult bindingResult,
//                      final Model model,
//                      final RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível cadastrar o Servico pois o formulário esta com erro.");
//            log.error("Não é possível cadastrar o Servico pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
//            if (ServicoDTO.getCategoriaId() != null) {
//                model.addAttribute("subCategoriaValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(ServicoDTO.getCategoriaId())));
//            } else {
//                model.addAttribute("subCategoriaValues", emptyMap());
//            }
//            return "Servico/add";
//        }
//
//        service.create(mapper.dtoToEntity(ServicoDTO));
//
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("Servico.create.success"));
//
//        return "redirect:/Servicos";
//    }
//
//    @GetMapping("/edit/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'Servico_EDITAR')")
//    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
//
//        ServicoDTO Servico = mapper.entityToDTO(service.findById(id));
//
//        model.addAttribute("Servico", Servico);
//
//        return "Servico/edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'Servico_EDITAR')")
//    public String edit(@PathVariable(name = "id") final Long id,
//                       @ModelAttribute("Servico") @Valid final ServicoDTO ServicoDTO,
//                       final BindingResult bindingResult,
//                       final Model model,
//                       final RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar o Servico pois o formulário esta com erro.");
//            log.error("Não é possível editar o Servico pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
//            if (ServicoDTO.getCategoriaId() != null) {
//                model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(ServicoDTO.getCategoriaId())));
//            } else {
//                model.addAttribute("subCategoriaIdValues", emptyMap());
//            }
//            return "Servico/edit";
//        }
//
//        service.update(id, mapper.dtoToEntity(ServicoDTO));
//
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("Servico.update.success"));
//
//        return "redirect:/Servicos";
//    }
//
//    @PostMapping("/delete/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'Servico_DELETAR')")
//    public String delete(@PathVariable(name = "id") final Long id,
//                         final RedirectAttributes redirectAttributes) {
//        try {
//
//            service.delete(id);
//
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("Servico.delete.success"));
//
//        } catch (final ReferencedException referencedException) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
//                    referencedException.getKey(), referencedException.getParams().toArray()));
//        }
//
//        return "redirect:/Servicos";
//    }
//
//}
