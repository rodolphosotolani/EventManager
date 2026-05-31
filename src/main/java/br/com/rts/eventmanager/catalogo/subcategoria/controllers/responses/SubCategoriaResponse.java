package br.com.rts.eventmanager.catalogo.subcategoria.controllers.responses;

import br.com.rts.eventmanager.catalogo.categoria.controllers.responses.CategoriaResponse;

public record SubCategoriaResponse(Long id,
                                   Long instituicao,
                                   String nome,
                                   Boolean ativo,
                                   CategoriaResponse categoria) {

}
