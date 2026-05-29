package br.com.rts.eventmanager.catalogo.produto.services.impl;

import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.repositories.ProdutoRepository;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repository;
    private final EstoqueService estoqueService;

    public List<Produto> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
    }

    public Produto findById(final Long produtoId) {
        return repository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado!"));
    }

    @Override
    public Long create(Produto produtoNew) {
        return repository.save(produtoNew).getId();
    }

    @Override
    public void update(Long id, Produto produtoNew) {
        Produto produto = this.findById(id);
        produto.setNome(produtoNew.getNome());
        produto.setEspecificacao(produtoNew.getEspecificacao());
        produto.setValorVendaUnitario(produtoNew.getValorVendaUnitario());
        produto.setQuantidadeMinima(produtoNew.getQuantidadeMinima());
        repository.save(produto);
    }

    public void delete(final Long produtoId) {

        final Produto produto = this.findById(produtoId);

        repository.delete(produto);
    }

    @Override
    public List<Produto> findAllByCategoriaIdAndSubCategoriaId(Long categoriaId, Long subCategoriaId) {
        List<Produto> produtos;
        if (categoriaId != null && subCategoriaId != null) {
            produtos = repository.findAllByCategoriaIdAndSubCategoriaId(categoriaId, subCategoriaId, Sort.by(Sort.Direction.ASC, "nome"));
        } else if (categoriaId != null) {
            produtos = repository.findAllByCategoriaId(categoriaId, Sort.by(Sort.Direction.ASC, "nome"));
        } else {
            produtos = repository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
        }
        return produtos;
    }

    @Override
    public Integer quantidadeEstoqueProduto(Produto produto) {
        return estoqueService.quantidadeEstoqueByProdutoId(produto.getId());
    }

}
