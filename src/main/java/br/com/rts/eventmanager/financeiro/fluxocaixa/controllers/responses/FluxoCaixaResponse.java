package br.com.rts.eventmanager.financeiro.fluxocaixa.controllers.responses;

import br.com.rts.eventmanager.financeiro.fluxocaixa.enumerators.TipoFluxoCaixaEnum;
import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;

import java.time.LocalDateTime;

public record FluxoCaixaResponse(
        Long id,
        Long instituicao,
        Long evento,
        Long caixaId,
        Long vendaId,
        TipoFluxoCaixaEnum tipoFluxoCaixa,
        FormaPagamentoEnum formaPagamento,
        String observacao,
        LocalDateTime dataVenda,
        Long usuario
) {
}
