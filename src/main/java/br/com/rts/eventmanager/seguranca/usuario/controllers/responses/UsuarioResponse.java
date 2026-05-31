package br.com.rts.eventmanager.seguranca.usuario.controllers.responses;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String email,
        String nome,
        String urlFoto,
        boolean ativo,
        LocalDateTime ultimoAcesso
) {
}
