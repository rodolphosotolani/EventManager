package br.com.rts.eventmanager.seguranca.perfilusuario.controllers.responses;

public record PerfilUsuarioResponse(Long id,
                                    Long usuarioId,
                                    Long perfilId,
                                    Long instituicao) {
}
