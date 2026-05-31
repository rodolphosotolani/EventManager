package br.com.rts.eventmanager.financeiro.cliente.services;

import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteService {

    Page<Cliente> findAllByInstituicao(Long instituicaoId, Pageable pageable);

    Optional<Cliente> findByIdAndInstituicao(Long id, Long instituicaoId);

    Cliente create(Long instituicaoId, Cliente request);

    void update(Long id, Long instituicaoId, Cliente request);

    void delete(Long id, Long instituicaoId);
}
