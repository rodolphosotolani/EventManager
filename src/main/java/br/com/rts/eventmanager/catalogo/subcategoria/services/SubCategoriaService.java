package br.com.rts.eventmanager.catalogo.subcategoria.services;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubCategoriaService {

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
}
