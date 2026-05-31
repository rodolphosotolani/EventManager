package br.com.rts.eventmanager.catalogo.produto.services.impl;

import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.repositories.ProdutoRepository;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repository;

    @Override
    public Page<Produto> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Produto findByIdAndInstituicaoAndEvento(Long produtoId, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId);
    }

    @Override
    public Produto create(Produto produtoNew, Long instituicaoId, Long eventoId) {
        return repository.save(produtoNew);
    }

    @Override
    public Produto update(Long produtoId, Produto produtoNew, Long instituicaoId, Long eventoId) {
        Produto produto = this.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId);

        produto.setNome(produtoNew.getNome());
        produto.setEspecificacao(produtoNew.getEspecificacao());
        produto.setValorVendaUnitario(produtoNew.getValorVendaUnitario());
        produto.setQuantidadeMinima(produtoNew.getQuantidadeMinima());

        if (produtoNew.getCategoria() != null) {
            produto.setCategoria(produtoNew.getCategoria());
        }

        if (produtoNew.getSubCategoria() != null) {
            produto.setSubCategoria(produtoNew.getSubCategoria());
        }

        return repository.save(produto);
    }

    @Override
    public void delete(Long produtoId, Long instituicaoId, Long eventoId) {
        final Produto produto = this.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId);

        repository.delete(produto);

    }

}
