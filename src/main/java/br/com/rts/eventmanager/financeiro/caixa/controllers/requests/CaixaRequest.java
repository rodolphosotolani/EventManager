package br.com.rts.eventmanager.financeiro.caixa.controllers.requests;

import br.com.rts.eventmanager.financeiro.caixa.enumerators.StatusCaixaEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CaixaRequest(@NotNull Long evento,
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
