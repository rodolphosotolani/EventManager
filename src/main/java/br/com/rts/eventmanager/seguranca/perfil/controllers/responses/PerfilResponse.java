package br.com.rts.eventmanager.seguranca.perfil.controllers.responses;

import br.com.rts.eventmanager.seguranca.permissao.controllers.responses.PermissaoResponse;

import java.util.Set;

public record PerfilResponse(
        Long id,
        Long instituicao,
        String nome,
        Set<PermissaoResponse> permissoes
) {
}
