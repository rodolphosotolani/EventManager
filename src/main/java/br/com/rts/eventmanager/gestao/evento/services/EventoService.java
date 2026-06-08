package br.com.rts.eventmanager.gestao.evento.services;

import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventoService {

    Page<Evento> findAllByInstituicao(@NotNull final Long instituicaoId,
                                      Pageable pageable);

    Optional<Evento> findByIdAndInstituicao(@NotNull final Long eventoId,
                                            @NotNull final Long instituicaoId);

    Evento create(@NotNull final Long instituicaoId,
                  Evento request);

    void update(@NotNull final Long eventoId,
                @NotNull final Long instituicaoId,
                Evento request);

    void delete(@NotNull final Long eventoId,
                @NotNull final Long instituicaoId);

    Boolean existsByInstituicaoAndId(@NotNull final Long instituicaoId,
                                     @NotNull final Long eventoId);

    List<Evento> findAllByInstituicao(Long instituicaoId);
}
