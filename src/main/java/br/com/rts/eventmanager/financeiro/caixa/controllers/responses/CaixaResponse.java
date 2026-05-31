package br.com.rts.eventmanager.financeiro.caixa.controllers.responses;

import br.com.rts.eventmanager.financeiro.caixa.enumerators.StatusCaixaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CaixaResponse(
        Long id,
        UUID uuid,
        Long instituicao,
        Long evento,
        Long usuarioAbertura,
        Long usuarioFechamento,
        StatusCaixaEnum statusCaixa,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        BigDecimal saldoInicial,
        BigDecimal saldoFinalCalculado,
        BigDecimal saldoFinalInformado,
        BigDecimal diferencaCaixa) {
}
