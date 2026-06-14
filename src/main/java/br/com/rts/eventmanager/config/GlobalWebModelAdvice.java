package br.com.rts.eventmanager.config;

import br.com.rts.eventmanager.gestao.EventoDTO;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.gestao.InstituicaoDTO;
import br.com.rts.eventmanager.seguranca.SegurancaFacade;
import br.com.rts.eventmanager.seguranca.UsuarioDTO;
import br.com.rts.eventmanager.seguranca.UsuarioInstituicaoDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = org.springframework.stereotype.Controller.class)
@RequiredArgsConstructor
public class GlobalWebModelAdvice {

    private final GestaoFacade gestaoFacade;

    private final SegurancaFacade segurancaFacade;

    @ModelAttribute
    public void populateGlobalModel(HttpSession session,
                                    @RequestParam(required = false) Long instituicaoId,
                                    @RequestParam(required = false) Long eventoId,
                                    Model model) {

        // 1. Resolve logged-in Google user and their access scope
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        String nome = null;
        String urlFoto = null;

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth) {
                email = oauth.getAttribute("email");
                nome = oauth.getAttribute("name");
                if (nome == null) {
                    nome = oauth.getAttribute("given_name");
                }
                if (nome == null && email != null) {
                    nome = email.split("@")[0];
                }
                urlFoto = oauth.getAttribute("picture");
            }
        }

        if (email != null) {
            model.addAttribute("usuarioLogado", new UsuarioLogadoInfo(nome, email, urlFoto));

            // Fetch allowed institutions based strictly on UsuarioInstituicao links
            List<InstituicaoDTO> allowedInstituicoes;
            Optional<UsuarioDTO> usuarioOpt = segurancaFacade.findFetchUsuarioByEmail(email);
            if (usuarioOpt.isPresent() && usuarioOpt.get().usuarioInstituicaos() != null) {
                allowedInstituicoes = usuarioOpt.get().usuarioInstituicaos().stream()
                        .map(UsuarioInstituicaoDTO::instituicao)
                        .filter(Objects::nonNull)
                        .distinct()
                        .map(gestaoFacade::getInstituicaoById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } else {
                allowedInstituicoes = java.util.Collections.emptyList();
            }

            model.addAttribute("instituicoes", allowedInstituicoes);

            // 2. Resolve active institution based on allowed scope
            if (instituicaoId != null) {
                if (instituicaoId == -1L) {
                    session.removeAttribute("activeInstituicaoId");
                    session.removeAttribute("activeEventoId");
                } else {
                    // Verify requested institution is within allowed list
                    boolean isAllowed = allowedInstituicoes.stream().anyMatch(i -> i.id().equals(instituicaoId));
                    if (isAllowed) {
                        session.setAttribute("activeInstituicaoId", instituicaoId);
                        session.removeAttribute("activeEventoId");
                    }
                }
            }

            Long activeInstId = (Long) session.getAttribute("activeInstituicaoId");
            InstituicaoDTO tenant = null;
            if (activeInstId != null) {
                tenant = allowedInstituicoes.stream()
                        .filter(i -> i.id().equals(activeInstId))
                        .findFirst()
                        .orElse(null);
            }

            model.addAttribute("tenant", tenant);
            populateTenantColors(tenant, model);

            // 3. Resolve active event for the selected institution
            if (tenant != null) {
                if (eventoId != null) {
                    gestaoFacade.validateIfInstituicaoAndEventoIsValid(tenant.id(), eventoId);
                    session.setAttribute("activeEventoId", eventoId);
                }

                Long activeEvId = (Long) session.getAttribute("activeEventoId");
                List<EventoDTO> eventos = gestaoFacade.findAllEventoByInstituicaoId(tenant.id());
                model.addAttribute("eventos", eventos);

                EventoDTO activeEvent = null;
                if (activeEvId != null) {
                    activeEvent = eventos.stream()
                            .filter(eventoDTO -> Objects.equals(eventoDTO.id(), activeEvId))
                            .findFirst()
                            .orElse(null);
                }

                if (activeEvent == null && !eventos.isEmpty()) {
                    activeEvent = eventos.getFirst();
                    session.setAttribute("activeEventoId", activeEvent.id());
                }

                model.addAttribute("activeEvent", activeEvent);
            } else {
                model.addAttribute("eventos", java.util.Collections.emptyList());
                model.addAttribute("activeEvent", null);
            }

        } else {
            // User is not authenticated
            model.addAttribute("usuarioLogado", null);
            model.addAttribute("instituicoes", java.util.Collections.emptyList());
            model.addAttribute("tenant", null);
            model.addAttribute("activeEvent", null);
        }
    }

    private void populateTenantColors(InstituicaoDTO tenant, Model model) {
        if (tenant != null) {
            if (tenant.id() == 1L) {
                // Stitch Base: orange theme
                model.addAttribute("tenantColorPrimary", "#e65100");
                model.addAttribute("tenantColorSecondary", "#bf360c");
                model.addAttribute("tenantColorPrimaryContainer", "#cd4700");
            } else {
                // Dynamic Indigo theme for other tenants
                model.addAttribute("tenantColorPrimary", "#1e3a8a");
                model.addAttribute("tenantColorSecondary", "#3b82f6");
                model.addAttribute("tenantColorPrimaryContainer", "#2563eb");
            }
        }
    }

    public record UsuarioLogadoInfo(String nome, String email, String urlFoto) {
    }
}
