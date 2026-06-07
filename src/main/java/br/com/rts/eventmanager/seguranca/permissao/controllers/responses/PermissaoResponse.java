package br.com.rts.eventmanager.seguranca.permissao.controllers.responses;

public record PermissaoResponse(
        Long id,
        String tela,
        String acao,
        String authority
) {
}
