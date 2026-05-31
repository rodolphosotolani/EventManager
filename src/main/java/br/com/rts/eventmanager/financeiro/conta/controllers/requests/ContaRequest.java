package br.com.rts.eventmanager.financeiro.conta.controllers.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContaRequest(@NotNull Long eventoId,
                           Long clienteId,
                           Long vendaId,
                           BigDecimal saldoDevedor,
                           Boolean pago,
                           LocalDateTime dataPagamento) {
}
