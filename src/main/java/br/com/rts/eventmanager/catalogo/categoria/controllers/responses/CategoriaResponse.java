package br.com.rts.eventmanager.catalogo.categoria.controllers.responses;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;

import java.util.List;

public record CategoriaResponse(Long id,
                                Long instituicao,
                                String nome,
                                Boolean ativo,
                                List<SubCategoria> subCategorias) {
}
