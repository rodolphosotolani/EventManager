package br.com.rts.eventmanager.catalogo.servico.services;

import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicoService {

    Page<Servico> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                @NotNull final Long eventoId,
                                                Pageable pageable);

    Servico findByInstituicaoAndEvento(@NotNull final Long servicoId,
                                       @NotNull final Long instituicaoId,
                                       @NotNull final Long eventoId);

    Servico create(Servico servico,
                   @NotNull final Long instituicaoId,
                   @NotNull final Long eventoId);

    Servico update(@NotNull final Long servicoId,
                   Servico servicoUpdate,
                   @NotNull final Long instituicaoId,
                   @NotNull final Long eventoId);

    void delete(@NotNull final Long servicoId,
                @NotNull final Long instituicaoId,
                @NotNull final Long eventoId);
}
