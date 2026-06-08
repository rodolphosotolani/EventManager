package br.com.rts.eventmanager.financeiro;

import java.math.BigDecimal;

public record VendaSumarioDTO(BigDecimal totalValor,
                              Long totalItens,
                              Long totalVendas) {
}
