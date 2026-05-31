package br.com.rts.eventmanager.financeiro.conta.services;

import br.com.rts.eventmanager.financeiro.conta.entities.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContaService {

    Page<Conta> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Optional<Conta> findByIdAndInstituicao(Long id, Long instituicaoId);

    Optional<Conta> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId);

    Conta get(Long id);

    Conta create(Conta request, Long instituicaoId);

    Conta update(Long id, Conta request, Long instituicaoId);

    void delete(Long id);
}
