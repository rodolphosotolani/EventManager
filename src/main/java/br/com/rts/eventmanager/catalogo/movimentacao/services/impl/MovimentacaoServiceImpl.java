package br.com.rts.eventmanager.catalogo.movimentacao.services.impl;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.movimentacao.repositories.MovimentacaoRepository;
import br.com.rts.eventmanager.catalogo.movimentacao.services.MovimentacaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Movimentacao create(Movimentacao movimentacao) {
        return movimentacaoRepository.save(movimentacao);
    }

    @Override
    public Movimentacao update(Long id, Movimentacao movimentacaoNew) {
        final Movimentacao movimentacao = this.get(id);
        movimentacao.setQuantidade(movimentacaoNew.getQuantidade());
        movimentacao.setTipoMovimentacao(movimentacaoNew.getTipoMovimentacao());
        movimentacao.setMotivoMovimentacao(movimentacaoNew.getMotivoMovimentacao());
        movimentacao.setValorUnitario(movimentacaoNew.getValorUnitario());
        movimentacao.setDataMovimentacao(movimentacaoNew.getDataMovimentacao());
        if (movimentacaoNew.getProduto() != null) {
            movimentacao.setProduto(movimentacaoNew.getProduto());
        }
        if (movimentacaoNew.getEstoque() != null) {
            movimentacao.setEstoque(movimentacaoNew.getEstoque());
        }
        return movimentacaoRepository.save(movimentacao);
    }

    public void delete(final Long id) {
        final Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        movimentacaoRepository.delete(movimentacao);
    }

    @Override
    public Page<Movimentacao> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable) {
        return movimentacaoRepository.findAllByInstituicaoAndEvento(instituicao, evento, pageable);
    }

    @Override
    public Movimentacao findByIdAndInstituicao(Long id, Long instituicao) {
        return movimentacaoRepository.findByIdAndInstituicao(id, instituicao);
    }

    @Override
    public Movimentacao findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento) {
        return movimentacaoRepository.findByIdAndInstituicaoAndEvento(id, instituicao, evento);
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
