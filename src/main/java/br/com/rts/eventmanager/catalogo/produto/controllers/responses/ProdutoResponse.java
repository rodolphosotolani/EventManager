package br.com.rts.eventmanager.catalogo.produto.controllers.responses;

import br.com.rts.eventmanager.catalogo.categoria.controllers.responses.CategoriaResponse;
import br.com.rts.eventmanager.catalogo.subcategoria.controllers.SubCategoriaResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResponse(Long id,
                              UUID uuid,
                              Long instituicao,
                              Long evento,
                              String nome,
                              String especificacao,
                              BigDecimal valorVendaUnitario,
                              Integer quantidadeMinima,
                              CategoriaResponse categoria,
                              SubCategoriaResponse subCategoria) {
}
