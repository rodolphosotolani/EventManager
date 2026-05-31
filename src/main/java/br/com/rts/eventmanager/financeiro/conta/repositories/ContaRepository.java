package br.com.rts.eventmanager.financeiro.conta.repositories;

import br.com.rts.eventmanager.financeiro.conta.entities.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByUuid(UUID uuid);

    Page<Conta> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable);

    Optional<Conta> findByIdAndInstituicao(Long id, Long instituicao);

    Optional<Conta> findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);
}
