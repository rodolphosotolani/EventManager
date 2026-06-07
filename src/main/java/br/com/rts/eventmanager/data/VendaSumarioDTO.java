package br.com.rts.eventmanager.data;

import java.math.BigDecimal;

public record VendaSumarioDTO(BigDecimal totalValor,
                              Long totalItens,
                              Long totalVendas) {
}
