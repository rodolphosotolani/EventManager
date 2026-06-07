package br.com.rts.eventmanager.config;

import br.com.rts.eventmanager.data.EventoDTO;
import br.com.rts.eventmanager.data.InstituicaoDTO;
import br.com.rts.eventmanager.data.PerfilUsuarioDTO;
import br.com.rts.eventmanager.data.UsuarioDTO;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.seguranca.SegurancaFacade;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
        OidcUser oidcUser = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OidcUser) {
            oidcUser = (OidcUser) authentication.getPrincipal();
        }

        if (oidcUser != null) {
            //TODO Substitua por DTO do usuario logado, buscando na base
            String email = oidcUser.getEmail();
            String nome = oidcUser.getFullName() != null ? oidcUser.getFullName() : (oidcUser.getGivenName() != null ? oidcUser.getGivenName() : email.split("@")[0]);
            model.addAttribute("usuarioLogado", new UsuarioLogadoInfo(nome, email, oidcUser.getPicture()));

            // Check if user is MASTER
            boolean isMaster = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_MASTER".equals(a.getAuthority()));

            List<InstituicaoDTO> allowedInstituicoes;
            if (isMaster) {
                allowedInstituicoes = gestaoFacade.findAllInstituicao();
            } else {
                // Fetch allowed institutions from user's profiles
                Optional<UsuarioDTO> usuarioOpt = segurancaFacade.findUsuarioByEmail(email);
                if (usuarioOpt.isPresent()) {
                    allowedInstituicoes = usuarioOpt.get().perfilUsuarios().stream()
                            .map(PerfilUsuarioDTO::instituicao)
                            .filter(java.util.Objects::nonNull)
                            .distinct()
                            .map(instId -> gestaoFacade.findInstituicaoById(instId)
                                    .orElse(null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                } else {
                    allowedInstituicoes = java.util.Collections.emptyList();
                }
            }

            model.addAttribute("instituicoes", allowedInstituicoes);

            // 2. Resolve active institution based on allowed scope
            if (instituicaoId != null) {
                // Verify requested institution is within allowed list
                boolean isAllowed = allowedInstituicoes.stream().anyMatch(i -> i.id().equals(instituicaoId));
                if (isAllowed) {
                    session.setAttribute("activeInstituicaoId", instituicaoId);
                    session.removeAttribute("activeEventoId");
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

            if (tenant == null && !allowedInstituicoes.isEmpty()) {
                tenant = allowedInstituicoes.get(0);
                session.setAttribute("activeInstituicaoId", tenant.id());
            }

            model.addAttribute("tenant", tenant);
            populateTenantColors(tenant, model);

            // 3. Resolve active event for the selected institution
            if (tenant != null) {
                if (eventoId != null) {
                    if (gestaoFacade.existsEventoByInstituicaoAndId(tenant.id(), eventoId)) {
                        session.setAttribute("activeEventoId", eventoId);
                    }
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
                    activeEvent = eventos.get(0);
                    session.setAttribute("activeEventoId", activeEvent.id());
                }

                model.addAttribute("activeEvent", activeEvent);
            } else {
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
