package br.com.rts.eventmanager.seguranca.usuarioinstituicao.controllers.responses;

import java.time.LocalDateTime;

public record UsuarioInstituicaoResponse(
        Long id,
        String email,
        String nome,
        String urlFoto,
        boolean ativo,
        LocalDateTime ultimoAcesso
) {
}
