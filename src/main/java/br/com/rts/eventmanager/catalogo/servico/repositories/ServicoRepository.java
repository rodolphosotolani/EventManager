package br.com.rts.eventmanager.catalogo.servico.repositories;

import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    Page<Servico> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Optional<Servico> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId);

}
