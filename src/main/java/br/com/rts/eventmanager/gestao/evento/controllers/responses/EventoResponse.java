package br.com.rts.eventmanager.gestao.evento.controllers.responses;

public record EventoResponse(Long id,
                             String nome,
                             Boolean ativo,
                             Long instituicaoId) {
}
