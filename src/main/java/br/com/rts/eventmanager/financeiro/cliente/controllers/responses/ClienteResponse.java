package br.com.rts.eventmanager.financeiro.cliente.controllers.responses;

import java.util.UUID;

public record ClienteResponse(Long id,
                              UUID uuid,
                              Long instituicao,
                              String nome,
                              String apelido,
                              String celular,
                              String telefone,
                              String email) {
}
