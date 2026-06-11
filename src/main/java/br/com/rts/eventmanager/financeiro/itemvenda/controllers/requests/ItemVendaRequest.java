package br.com.rts.eventmanager.financeiro.itemvenda.controllers.requests;

import jakarta.validation.constraints.NotNull;

public record ItemVendaRequest(@NotNull Long evento,
                               Long produtoId,
                               Long servicoId,
                               Long vendaId,
                               Integer quantidade) {
}
