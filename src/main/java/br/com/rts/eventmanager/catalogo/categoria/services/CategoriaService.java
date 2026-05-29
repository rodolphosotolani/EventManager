package br.com.rts.eventmanager.catalogo.categoria.services;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;

import java.util.List;

public interface CategoriaService {

    List<Categoria> findAll();

    Categoria findById(final Long categoriaId);

    Long create(final Categoria categoriaNew);

    void update(final Long id, final Categoria categoriaNew);

    void delete(final Long categoriaId);
}
