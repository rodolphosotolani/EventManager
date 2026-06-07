package br.com.rts.eventmanager.financeiro.venda.specs;

import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FiltroVendas(FormaPagamentoEnum formaPagamento,
                           Boolean vendido,
                           BigDecimal valorMin,
                           BigDecimal valorMax,
                           LocalDate dataInicio,
                           LocalDate dataFim,
                           Long produtoId) {
}
