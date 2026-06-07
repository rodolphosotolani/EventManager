package br.com.rts.eventmanager.financeiro.venda.repositories;

import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long>, JpaSpecificationExecutor<Venda> {

    Page<Venda> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable);

    Optional<Venda> findByIdAndInstituicao(Long id, Long instituicao);

    Optional<Venda> findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);
}
