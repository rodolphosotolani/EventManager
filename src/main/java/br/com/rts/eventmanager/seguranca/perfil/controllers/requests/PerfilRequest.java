package br.com.rts.eventmanager.seguranca.perfil.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record PerfilRequest(
        @NotNull Long instituicao,
        @NotBlank String nome,
        Set<Long> permissaoIds
) {
}
