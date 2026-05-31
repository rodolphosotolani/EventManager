package br.com.rts.eventmanager.financeiro.itemvenda.repositories;

import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {

    Page<ItemVenda> findAllByInstituicaoAndEventoAndVendaId(Long instituicao, Long evento, Long vendaId, Pageable pageable);

    Optional<ItemVenda> findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);
}
