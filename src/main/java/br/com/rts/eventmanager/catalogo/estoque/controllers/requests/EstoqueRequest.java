package br.com.rts.eventmanager.catalogo.estoque.controllers.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EstoqueRequest(@NotNull Long evento,
                             Long produtoId,
                             Integer quantidadeInicial,
                             BigDecimal valorCompraUnitario) {
}
