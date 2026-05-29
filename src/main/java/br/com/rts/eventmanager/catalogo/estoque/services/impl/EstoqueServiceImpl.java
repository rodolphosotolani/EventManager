package br.com.rts.eventmanager.catalogo.estoque.services.impl;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.estoque.repositories.EstoqueRepository;
import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueServiceImpl implements EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public List<Estoque> findAll() {
        return estoqueRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Estoque get(final Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estoque não encontrado!"));
    }

    @Override
    public Long create(Estoque estoque) {
        if (estoque.getQuantidadeInicial() == null) {
            estoque.setQuantidadeInicial(estoque.getQuantidadeAtual());
        }
        return estoqueRepository.save(estoque).getId();
    }

    @Override
    public void update(Long id, Estoque estoqueNew) {
        final Estoque estoque = this.get(id);
        estoque.setQuantidadeAtual(estoqueNew.getQuantidadeAtual());
        estoque.setValorCompraUnitario(estoqueNew.getValorCompraUnitario());
        estoqueRepository.save(estoque);
    }

    public void delete(final Long id) {
        final Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        estoqueRepository.delete(estoque);
    }

    private Estoque findEstoqueByProdutoId(Long produtoId) {

        return estoqueRepository
                .findFirstByProdutoId(produtoId)
                .orElseThrow(() -> new NotFoundException("Estoque para o produto não encontrado!"));
    }

//    @Override
//    public void registrarSaidaEstoque(ItemVenda itemVenda) {
//
//        Estoque estoque = this.findEstoqueByProdutoId(itemVenda.getProduto().getId());
//
//        estoque.setQuantidade(estoque.getQuantidade() - itemVenda.getQuantidade());
//
//        estoqueRepository.save(estoque);
//    }
//
//    @Override
//    public void registrarEntradaEstoque(ItemVenda itemVenda) {
//
//        Estoque estoque = this.findEstoqueByProdutoId(itemVenda.getProduto().getId());
//
//        estoque.setQuantidade(estoque.getQuantidade() + itemVenda.getQuantidade());
//
//        estoqueRepository.save(estoque);
//
//    }

    @Override
    public Integer quantidadeEstoqueByProdutoId(Long produtoId) {
        return estoqueRepository.sumQuantidadeByProdutoId(produtoId);
    }

    @Override
    public Double avgValorCompraByProdutoId(Long produtoId) {
        return estoqueRepository.avgValorCompraByProdutoId(produtoId);
    }

}
