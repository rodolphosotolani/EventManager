package br.com.rts.eventmanager.financeiro.venda.controllers.requests;

import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaRequest(Long evento,
                           BigDecimal valorTotal,
                           Boolean vendido,
                           FormaPagamentoEnum formaPagamento,
                           LocalDateTime dataVenda) {
}
