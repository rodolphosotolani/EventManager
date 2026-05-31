package br.com.rts.eventmanager.financeiro.venda.services;

import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VendaService {

    Page<Venda> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Optional<Venda> findByIdAndInstituicao(Long id, Long instituicaoId);

    Optional<Venda> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId);

    Venda get(Long id);

    Venda create(Venda request);

    Venda update(Long id, Venda request, Long instituicaoId);

    void delete(Long id);
}
