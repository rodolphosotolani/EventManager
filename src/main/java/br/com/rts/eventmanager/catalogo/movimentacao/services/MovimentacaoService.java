package br.com.rts.eventmanager.catalogo.movimentacao.services;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovimentacaoService {

    List<Movimentacao> findAll();

    Movimentacao get(final Long id);

    Movimentacao create(final Movimentacao movimentacao);

    Movimentacao update(final Long id, final Movimentacao movimentacaoNew);

    void delete(final Long id);

    Page<Movimentacao> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable);

    Movimentacao findByIdAndInstituicao(Long id, Long instituicao);

    Movimentacao findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);
}
