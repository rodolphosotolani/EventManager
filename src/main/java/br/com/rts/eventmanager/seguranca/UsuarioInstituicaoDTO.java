package br.com.rts.eventmanager.seguranca;

public record UsuarioInstituicaoDTO(Long id,
                                    Long usuarioId,
                                    Long instituicao,
                                    Boolean ativo) {
}
