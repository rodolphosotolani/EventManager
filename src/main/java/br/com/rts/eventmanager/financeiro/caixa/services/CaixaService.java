package br.com.rts.eventmanager.financeiro.caixa.services;

import br.com.rts.eventmanager.financeiro.caixa.entities.Caixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CaixaService {

    Page<Caixa> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Optional<Caixa> findByIdAndInstituicao(Long id, Long instituicaoId);

    Optional<Caixa> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId);

    Caixa get(Long id);

    Caixa create(Caixa request, Long instituicaoId);

    Caixa update(Long id, Caixa request, Long instituicaoId);

    void delete(Long id);
}
