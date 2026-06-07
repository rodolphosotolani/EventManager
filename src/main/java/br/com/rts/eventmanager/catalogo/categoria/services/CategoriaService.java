package br.com.rts.eventmanager.catalogo.categoria.services;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaService {

    Categoria create(final Long instituicaoId, final Categoria categoriaNew);

    Categoria update(final Long instituicaoId, final Long id, final Categoria categoriaNew);

    void delete(final Long instituicaoId, final Long categoriaId);

    Page<Categoria> findAllByInstituicao(final Long instituicaoId, Pageable pageable);

    List<Categoria> findAllByInstituicao(final Long instituicaoId);

    Categoria findByIdAndInstituicao(final Long categoriaId, final Long instituicaoId);
}
