package br.com.rts.eventmanager.catalogo.produto.services;

import br.com.rts.eventmanager.catalogo.produto.entities.Produto;

import java.util.List;

public interface ProdutoService {

    List<Produto> findAll();

    Produto findById(final Long produtoId);

    Long create(final Produto produtoNew);

    void update(final Long id, final Produto produtoNew);

    void delete(final Long id);

    Integer quantidadeEstoqueProduto(Produto produto);

    List<Produto> findAllByCategoriaIdAndSubCategoriaId(Long categoriaId, Long subCategoriaId);
}
