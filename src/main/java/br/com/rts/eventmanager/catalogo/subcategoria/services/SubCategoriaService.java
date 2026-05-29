package br.com.rts.eventmanager.catalogo.subcategoria.services;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;

import java.util.List;

public interface SubCategoriaService {

    SubCategoria findById(Long subCategoriaId);

    List<SubCategoria> findAll();

    List<SubCategoria> findAllByCategoria(Long categoriaId);

    Long create(final SubCategoria subCategoriaNew);

    void update(final Long id, final SubCategoria subCategoriaNew);

    void delete(final Long id);

}
