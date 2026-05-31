package br.com.rts.eventmanager.gestao.evento.repositories;

import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    Page<Evento> findAllByInstituicaoId(Long instituicaoId, Pageable pageable);

    Optional<Evento> findByIdAndInstituicaoId(Long id, Long instituicaoId);

    Boolean existsByInstituicaoIdAndId(Long instituicaoId, Long eventoId);
}
