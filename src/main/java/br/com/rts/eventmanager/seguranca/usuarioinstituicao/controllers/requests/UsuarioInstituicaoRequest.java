package br.com.rts.eventmanager.seguranca.usuarioinstituicao.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioInstituicaoRequest(
        @NotBlank @Email String email,
        @NotBlank String nome,
        String urlFoto,
        Boolean ativo
) {
}
