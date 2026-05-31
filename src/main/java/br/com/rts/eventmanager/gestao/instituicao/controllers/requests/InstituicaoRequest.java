package br.com.rts.eventmanager.gestao.instituicao.controllers.requests;

import jakarta.validation.constraints.NotNull;

public record InstituicaoRequest(@NotNull String nome,
                                 @NotNull Boolean ativo) {
}
