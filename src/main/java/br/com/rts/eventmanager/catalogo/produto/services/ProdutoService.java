package br.com.rts.eventmanager.catalogo.produto.services;

import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProdutoService {

    Page<Produto> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                @NotNull final Long eventoId,
                                                Pageable pageable);

    Produto findByIdAndInstituicaoAndEvento(@NotNull final Long produtoId,
                                            @NotNull final Long instituicaoId,
                                            @NotNull final Long eventoId);

    Produto create(final Produto produtoNew,
                   @NotNull final Long eventoId,
                   @NotNull final Long instituicaoId);

    Produto update(final Long produtoId,
                   final Produto produtoNew,
                   @NotNull final Long instituicaoId);

    void delete(@NotNull final Long produtoId,
                @NotNull final Long instituicaoId,
                @NotNull final Long eventoId);

    List<Produto> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                @NotNull final Long eventoId);

    List<Produto> findAllByInstituicaoAndEventoAndCategoriaId(@NotNull final Long instituicaoId,
                                                              @NotNull final Long eventoId,
                                                              @NotNull final Long categoriaId);

    List<Produto> findAllByInstituicaoAndEventoAndCategoriaIdAndSubCategoriaId(@NotNull final Long instituicaoId,
                                                                              @NotNull final Long eventoId,
                                                                              @NotNull final Long categoriaId,
                                                                              @NotNull final Long subCategoriaId);
}
