package br.com.rts.eventmanager.financeiro.caixa.repositories;

import br.com.rts.eventmanager.financeiro.caixa.entities.Caixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Page<Caixa> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable);

    Optional<Caixa> findByIdAndInstituicao(Long id, Long instituicao);

    Optional<Caixa> findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);
}
