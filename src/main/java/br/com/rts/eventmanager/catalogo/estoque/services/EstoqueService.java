package br.com.rts.eventmanager.catalogo.estoque.services;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EstoqueService {

    Page<Estoque> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                @NotNull final Long eventoId,
                                                Pageable pageable);

    Estoque findByIdAndInstituicaoAndEvento(@NotNull final Long estoqueId,
                                            @NotNull final Long instituicaoId,
                                            @NotNull final Long eventoId);

    Estoque create(final Estoque estoque,
                   @NotNull final Long instituicaoId);

    void delete(@NotNull final Long estoqueId,
                @NotNull final Long instituicaoId,
                @NotNull final Long eventoId);

    Estoque adicionaAoEstoque(@NotNull final Long estoqueId,
                              final Estoque estoqueNew,
                              @NotNull final Long instituicaoId);

    Estoque updateEstoque(@NotNull final Long estoqueId,
                   final Estoque estoqueUpdate,
                   @NotNull final Long instituicaoId);

    void subtrairEstoqueProduto(Long produtoId, Long instituicaoId, Long eventoId, int quantidade);

    Estoque subtrairDoEstoque(Long estoqueId, Estoque estoqueUpdate, Long instituicaoId);
}
