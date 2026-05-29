package br.com.rts.eventmanager.catalogo.estoque.services;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;

import java.util.List;

public interface EstoqueService {

    List<Estoque> findAll();

    Estoque get(final Long id);

    Long create(final Estoque estoque);

    void update(final Long id, final Estoque estoqueNew);

    void delete(final Long id);

    Integer quantidadeEstoqueByProdutoId(Long produtoId);

}
