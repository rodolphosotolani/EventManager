package br.com.rts.eventmanager.catalogo.estoque.controllers.requests;

import java.math.BigDecimal;

public record EstoqueRequest(Long produtoId,
                             Integer quantidadeInicial,
                             BigDecimal valorCompraUnitario) {
}
