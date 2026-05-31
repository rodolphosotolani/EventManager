package br.com.rts.eventmanager.catalogo.produto.controllers.requests;

import java.math.BigDecimal;

public record ProdutoRequest(String nome,
                             String especificacao,
                             BigDecimal valorVendaUnitario,
                             Integer quantidadeMinima,
                             Long categoriaId,
                             Long subCategoriaId) {

}
