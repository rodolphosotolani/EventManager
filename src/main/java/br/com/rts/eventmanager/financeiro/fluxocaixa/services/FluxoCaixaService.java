package br.com.rts.eventmanager.financeiro.fluxocaixa.services;

import br.com.rts.eventmanager.financeiro.fluxocaixa.entities.FluxoCaixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FluxoCaixaService {

    Page<FluxoCaixa> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Optional<FluxoCaixa> findByIdAndInstituicao(Long id, Long instituicaoId);

    Optional<FluxoCaixa> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId);

    FluxoCaixa get(Long id);

    FluxoCaixa create(FluxoCaixa request);

    FluxoCaixa update(Long id, FluxoCaixa request, Long instituicaoId);

    void delete(Long id);
}
