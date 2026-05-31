package br.com.rts.eventmanager.seguranca.usuario.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
        @NotBlank @Email String email,
        @NotBlank String nome,
        String urlFoto,
        Boolean ativo
) {
}
