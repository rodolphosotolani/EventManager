package br.com.rts.eventmanager.seguranca.usuarioinstituicao.controllers;

import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.gestao.InstituicaoDTO;
import br.com.rts.eventmanager.seguranca.perfilusuario.services.PerfilUsuarioService;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.entities.UsuarioInstituicao;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.services.UsuarioInstituicaoService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios-instituicao")
@RequiredArgsConstructor
public class UsuarioInstituicaoController {

    private final UsuarioInstituicaoService service;
    private final UsuarioService usuarioService;
    private final PerfilUsuarioService perfilUsuarioService;

    private final GestaoFacade gestaoFacade;

    @GetMapping("/edit/{usuarioId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public String edit(@PathVariable Long usuarioId,
                       HttpSession session,
                       Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");

        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        Usuario usuario = usuarioService.getUsuarioById(usuarioId);
        List<InstituicaoDTO> todasInstituicoes = gestaoFacade.findAllInstituicao();

        Map<Long, String> instituicaoNomes = todasInstituicoes.stream()
                .collect(Collectors.toMap(InstituicaoDTO::id, InstituicaoDTO::nome));

        Set<Long> activeInstituicaoIds = usuario.getUsuarioInstituicaos().stream()
                .map(UsuarioInstituicao::getInstituicao)
                .collect(Collectors.toSet());

        List<InstituicaoDTO> disponiveis = todasInstituicoes.stream()
                .filter(inst -> !activeInstituicaoIds.contains(inst.id()))
                .collect(Collectors.toList());

        model.addAttribute("usuario", usuario);
        model.addAttribute("instituicoesDisponiveis", disponiveis);
        model.addAttribute("instituicaoNomes", instituicaoNomes);
        model.addAttribute("pageTitle", "Associar Usuário a Instituições");
        return "usuario/edit";
    }

    @PostMapping("/edit/{usuarioId}/vincular")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public String vincularInstituicao(@PathVariable Long usuarioId,
                                      @RequestParam("instituicaoId") Long instituicaoId,
                                      @RequestParam(value = "perfilIds", required = false) List<Long> perfilIds,
                                      final RedirectAttributes redirectAttributes) {

        if (perfilIds == null || perfilIds.isEmpty()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Selecione ao menos um perfil para vincular!");
            return "redirect:/usuarios/edit/" + usuarioId;
        }

        service.linkToInstituicao(usuarioId, instituicaoId);

        perfilUsuarioService.assignPerfilToInstituicao(usuarioId, instituicaoId, perfilIds);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Instituição e perfis vinculados com sucesso!");
        return "redirect:/usuarios/edit/" + usuarioId;
    }

    @PostMapping("/edit/{id}/desvincular/{instituicaoId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'USUARIOS_EDITAR')")
    public String desvincularInstituicao(@PathVariable Long id,
                                         @PathVariable Long instituicaoId,
                                         final RedirectAttributes redirectAttributes) {

        service.unlinkToInstituicao(id, instituicaoId);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Associação inativada com sucesso!");

        return "redirect:/usuarios/edit/" + id;
    }

}
