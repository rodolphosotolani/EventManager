package br.com.rts.eventmanager.catalogo.produto.controllers.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequest(@NotNull Long evento,
                             @NotNull String nome,
                             String especificacao,
                             @NotNull BigDecimal valorVendaUnitario,
                             @NotNull Integer quantidadeMinima,
                             @NotNull Long categoriaId,
                             Long subCategoriaId) {

}
