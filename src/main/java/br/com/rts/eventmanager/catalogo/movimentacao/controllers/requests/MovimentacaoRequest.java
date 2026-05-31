package br.com.rts.eventmanager.catalogo.movimentacao.controllers.requests;

import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.MotivoMovimentacaoEnum;
import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.TipoMovimentacaoEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MovimentacaoRequest(
        @NotNull Long evento,
        Long estoqueId,
        Long produtoId,
        TipoMovimentacaoEnum tipoMovimentacao,
        MotivoMovimentacaoEnum motivoMovimentacao,
        Integer quantidade,
        BigDecimal valorUnitario) {

}
