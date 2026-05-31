package br.com.rts.eventmanager.catalogo.movimentacao.services;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimentacaoService {

    Page<Movimentacao> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                     @NotNull final Long eventoId,
                                                     Pageable pageable);

    Movimentacao findByIdAndInstituicao(@NotNull final Long movimentacaoId,
                                        @NotNull final Long instituicaoId,
                                        @NotNull final Long eventoId);

    Movimentacao create(Movimentacao movimentacao,
                        @NotNull final Long instituicaoId);

    Movimentacao update(@NotNull final Long movimentacaoId,
                        Movimentacao movimentacaoUpdate,
                        @NotNull final Long instituicaoId);

    void delete(@NotNull final Long movimentacaoId,
                @NotNull final Long instituicaoId,
                @NotNull final Long eventoId);
}
