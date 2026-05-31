package br.com.rts.eventmanager.financeiro.itemvenda.controllers.responses;

public record ItemVendaResponse(Long id,
                                Long instituicao,
                                Long evento,
                                Long produtoId,
                                Long vendaId,
                                Integer quantidade) {
}
