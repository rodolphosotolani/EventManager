package br.com.rts.eventmanager.financeiro.cliente.controllers;

import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import br.com.rts.eventmanager.financeiro.cliente.services.ClienteService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CLIENTES_LISTAR')")
    public String list(HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("clientes", service.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
        model.addAttribute("pageTitle", "Clientes");
        return "cliente/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CLIENTES_CADASTRAR')")
    public String add(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("pageTitle", "Novo Cliente");
        return "cliente/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CLIENTES_CADASTRAR')")
    public String add(HttpSession session,
                      @ModelAttribute("cliente") @Valid final Cliente cliente,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Novo Cliente");
            return "cliente/add";
        }

        cliente.setUuid(UUID.randomUUID());
        cliente.setInstituicao(tenantId);
        cliente.setDateCreated(java.time.LocalDateTime.now());
        cliente.setLastUpdated(java.time.LocalDateTime.now());

        service.create(tenantId, cliente);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Cliente cadastrado com sucesso!");
        return "redirect:/clientes";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CLIENTES_EDITAR')")
    public String edit(@PathVariable Long id, HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        Cliente cliente = service.findByIdAndInstituicao(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        model.addAttribute("cliente", cliente);
        model.addAttribute("pageTitle", "Editar Cliente");
        return "cliente/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CLIENTES_EDITAR')")
    public String edit(@PathVariable Long id,
                       HttpSession session,
                       @ModelAttribute("cliente") @Valid final Cliente cliente,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Cliente");
            return "cliente/edit";
        }

        Cliente existing = service.findByIdAndInstituicao(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        existing.setNome(cliente.getNome());
        existing.setApelido(cliente.getApelido());
        existing.setCelular(cliente.getCelular());
        existing.setTelefone(cliente.getTelefone());
        existing.setEmail(cliente.getEmail());
        existing.setLastUpdated(java.time.LocalDateTime.now());

        service.update(id, tenantId, existing);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Cliente atualizado com sucesso!");
        return "redirect:/clientes";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'CLIENTES_DELETAR')")
    public String delete(@PathVariable Long id, HttpSession session, final RedirectAttributes redirectAttributes) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId != null) {
            try {
                service.delete(id, tenantId);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Cliente excluído com sucesso!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não foi possível excluir o cliente pois ele está referenciado em contas ou vendas.");
            }
        }
        return "redirect:/clientes";
    }
}
