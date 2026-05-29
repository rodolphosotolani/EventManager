package br.com.rts.eventmanager.catalogo.servico.services;

import br.com.rts.eventmanager.catalogo.servico.entities.Servico;

import java.util.List;

public interface ServicoService {

    List<Servico> findAll();

    Servico findById(final Long produtoId);

    Long create(final Servico produtoNew);

    void update(final Long id, final Servico produtoNew);

    void delete(final Long id);

    List<Servico> findAllByCategoriaIdAndSubCategoriaId(Long categoriaId, Long subCategoriaId);
}
