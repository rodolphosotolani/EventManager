package br.com.rts.eventmanager.catalogo.servico.controllers;

import br.com.rts.eventmanager.catalogo.ServicoDTO;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import br.com.rts.eventmanager.catalogo.servico.mappers.ServicoMapper;
import br.com.rts.eventmanager.catalogo.servico.services.ServicoService;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService service;
    private final ServicoMapper mapper;
    private final CategoriaService categoriaService;
    private final SubCategoriaService subCategoriaService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SERVICOS_LISTAR')")
    public String list(HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (activeEvId != null) {
            model.addAttribute("servicos",
                    service.findAllByInstituicaoAndEvento(tenantId, activeEvId)
                            .stream()
                            .map(mapper::entityToDTO)
                            .toList());
        } else {
            model.addAttribute("servicos", java.util.Collections.emptyList());
        }

        model.addAttribute("pageTitle", "Serviços");
        return "servico/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SERVICOS_CADASTRAR')")
    public String add(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Nenhum evento ativo cadastrado! Apenas usuários Master/Admin podem criar eventos.");
            return "redirect:/servicos";
        }

        model.addAttribute("servico", new ServicoDTO());
        model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
        model.addAttribute("pageTitle", "Novo Serviço");

        return "servico/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SERVICOS_CADASTRAR')")
    public String add(HttpSession session,
                       @ModelAttribute("servico") @Valid final ServicoDTO servicoNovo,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Nenhum evento ativo cadastrado!");
            return "redirect:/servicos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
            if (servicoNovo.getCategoria() != null && servicoNovo.getCategoria().getId() != null) {
                model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, servicoNovo.getCategoria().getId()));
            }
            model.addAttribute("pageTitle", "Novo Serviço");
            return "servico/add";
        }

        Servico servico = mapper.dtoToEntity(servicoNovo);
        servico.setEvento(activeEvId);
        service.create(servico, tenantId);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Serviço cadastrado com sucesso!");
        return "redirect:/servicos";
    }

    @GetMapping("/edit/{servicoId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SERVICOS_EDITAR')")
    public String edit(@PathVariable Long servicoId, HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/servicos";
        }

        Servico servico = service.findByInstituicaoAndEvento(servicoId, tenantId, activeEvId);

        model.addAttribute("servico", mapper.entityToDTO(servico));
        model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
        if (servico.getCategoria() != null) {
            model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, servico.getCategoria().getId()));
        }
        model.addAttribute("pageTitle", "Editar Serviço");

        return "servico/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SERVICOS_EDITAR')")
    public String edit(@PathVariable Long id,
                       HttpSession session,
                       @ModelAttribute("servico") @Valid final ServicoDTO servico,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/servicos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
            if (servico.getCategoria() != null && servico.getCategoria().getId() != null) {
                model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, servico.getCategoria().getId()));
            }
            model.addAttribute("pageTitle", "Editar Serviço");
            return "servico/edit";
        }

        Servico servicoNew = mapper.dtoToEntity(servico);
        servicoNew.setEvento(activeEvId);

        service.update(id, servicoNew, tenantId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Serviço atualizado com sucesso!");
        return "redirect:/servicos";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'SERVICOS_DELETAR')")
    public String delete(@PathVariable Long id, HttpSession session, final RedirectAttributes redirectAttributes) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId != null && activeEvId != null) {
            try {
                service.delete(id, tenantId, activeEvId);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Serviço excluído com sucesso!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não foi possível excluir o serviço pois ele está referenciado em vendas.");
            }
        }
        return "redirect:/servicos";
    }
}
