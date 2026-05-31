package br.com.rts.eventmanager.gestao.evento.services;

import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EventoService {

    Page<Evento> findAllByInstituicao(Long instituicaoId, Pageable pageable);

    Optional<Evento> findByIdAndInstituicao(Long id, Long instituicaoId);

    Evento create(Long instituicaoId, Evento request);

    void update(Long id, Long instituicaoId, Evento request);

    void delete(Long id, Long instituicaoId);
}
