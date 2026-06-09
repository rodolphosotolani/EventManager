package br.com.rts.eventmanager.catalogo.subcategoria.services;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubCategoriaService {

    Page<SubCategoria> findAllByInstituicaoAndCategoria(@NotNull final Long instituicaoId,
                                                        @NotNull final Long categoriaId,
                                                        Pageable pageable);

    List<SubCategoria> findAllByInstituicaoAndCategoria(@NotNull final Long instituicaoId,
                                                        @NotNull final Long categoriaId);

    Page<SubCategoria> findAllByInstituicao(@NotNull final Long instituicaoId,
                                            Pageable pageable);

    SubCategoria findByIdAndInstituicao(@NotNull final Long subCategoriaId,
                                        @NotNull final Long instituicaoId);

    SubCategoria create(SubCategoria subCategoria,
                        @NotNull final Long instituicaoId);

    SubCategoria update(@NotNull final Long subCategoriaId,
                        SubCategoria subCategoriaUpdate,
                        @NotNull final Long instituicaoId);

    void delete(@NotNull final Long subCategoriaId,
                @NotNull final Long instituicaoId);

    void validIfSubCategoriaPertenceCategoria(@NotNull final Long subCategoriaId,
                                              @NotNull final Long categoriaId);
}
