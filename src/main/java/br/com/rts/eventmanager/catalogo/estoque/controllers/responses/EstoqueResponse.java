package br.com.rts.eventmanager.catalogo.estoque.controllers.responses;

import br.com.rts.eventmanager.catalogo.produto.controllers.responses.ProdutoResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record EstoqueResponse(Long id,
                              UUID uuid,
                              Long instituicao,
                              Long evento,
                              ProdutoResponse produto,
                              Integer quantidadeInicial,
                              Integer quantidadeAtual,
                              BigDecimal valorCompraUnitario) {

}
