package br.com.rts.eventmanager.financeiro.fluxocaixa.repositories;

import br.com.rts.eventmanager.financeiro.fluxocaixa.entities.FluxoCaixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FluxoCaixaRepository extends JpaRepository<FluxoCaixa, Long> {

    Page<FluxoCaixa> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable);

    Optional<FluxoCaixa> findByIdAndInstituicao(Long id, Long instituicao);

    Optional<FluxoCaixa> findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);
}
