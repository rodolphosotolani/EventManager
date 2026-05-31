package br.com.rts.eventmanager.financeiro.fluxocaixa.controllers.requests;

import br.com.rts.eventmanager.financeiro.fluxocaixa.enumerators.TipoFluxoCaixaEnum;
import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FluxoCaixaRequest(
        @NotNull Long evento,
        Long caixaId,
        Long vendaId,
        TipoFluxoCaixaEnum tipoFluxoCaixa,
        FormaPagamentoEnum formaPagamento,
        String observacao,
        LocalDateTime dataVenda,
        Long usuario
) {
}
