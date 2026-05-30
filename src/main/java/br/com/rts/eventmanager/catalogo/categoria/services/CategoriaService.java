package br.com.rts.eventmanager.catalogo.categoria.services;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaService {

    List<Categoria> findAll();

    Categoria create(final Long instituicaoId, final Categoria categoriaNew);

    Categoria update(final Long instituicaoId, final Long id, final Categoria categoriaNew);

    void delete(final Long instituicaoId, final Long categoriaId);

    @Nullable
    Page<Categoria> findAllByInstituicao(final Long instituicaoId, Pageable pageable);

    Categoria findByIdAndInstituicao(final Long categoriaId, final Long instituicaoId);
}
