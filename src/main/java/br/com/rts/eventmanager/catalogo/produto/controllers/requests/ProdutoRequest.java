package br.com.rts.eventmanager.catalogo.produto.controllers.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequest(
        @NotNull Long evento,
        String nome,
        String especificacao,
        BigDecimal valorVendaUnitario,
        Integer quantidadeMinima,
        Long categoriaId,
        Long subCategoriaId) {

}
