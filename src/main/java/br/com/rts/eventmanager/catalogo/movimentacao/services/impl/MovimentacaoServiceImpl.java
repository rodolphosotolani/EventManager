package br.com.rts.eventmanager.catalogo.movimentacao.services.impl;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.movimentacao.repositories.MovimentacaoRepository;
import br.com.rts.eventmanager.catalogo.movimentacao.services.MovimentacaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoServiceImpl implements MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;

    public List<Movimentacao> findAll() {
        return movimentacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Movimentacao get(final Long id) {
        return movimentacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movimentacao não encontrado!"));
    }

    @Override
    public Long create(Movimentacao movimentacao) {
        return movimentacaoRepository.save(movimentacao).getId();
    }

    @Override
    public void update(Long id, Movimentacao movimentacaoNew) {
        final Movimentacao movimentacao = this.get(id);
        movimentacao.setQuantidade(movimentacaoNew.getQuantidade());
        movimentacaoRepository.save(movimentacao);
    }

    public void delete(final Long id) {
        final Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        movimentacaoRepository.delete(movimentacao);
    }

//    @Override
//    public void registrarSaidaMovimentacao(ItemVenda itemVenda) {
//
//        Movimentacao movimentacao = this.findMovimentacaoByProdutoId(itemVenda.getProduto().getId());
//
//        movimentacao.setQuantidade(movimentacao.getQuantidade() - itemVenda.getQuantidade());
//
//        movimentacaoRepository.save(movimentacao);
//    }
//
//    @Override
//    public void registrarEntradaMovimentacao(ItemVenda itemVenda) {
//
//        Movimentacao movimentacao = this.findMovimentacaoByProdutoId(itemVenda.getProduto().getId());
//
//        movimentacao.setQuantidade(movimentacao.getQuantidade() + itemVenda.getQuantidade());
//
//        movimentacaoRepository.save(movimentacao);
//
//    }

}
