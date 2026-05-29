package br.com.rts.eventmanager.catalogo.movimentacao.services;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;

import java.util.List;

public interface MovimentacaoService {

    List<Movimentacao> findAll();

    Movimentacao get(final Long id);

    Long create(final Movimentacao movimentacao);

    void update(final Long id, final Movimentacao movimentacaoNew);

    void delete(final Long id);

//    void registrarSaidaMovimentacao(ItemVenda itemVenda);
//
//    void registrarEntradaMovimentacao(ItemVenda itemVenda);

}
