package br.com.rts.eventmanager.catalogo.estoque.repositories;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Page<Estoque> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable);

    Optional<Estoque> findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);

}
