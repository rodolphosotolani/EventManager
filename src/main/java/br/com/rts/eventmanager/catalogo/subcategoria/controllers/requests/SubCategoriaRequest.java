package br.com.rts.eventmanager.catalogo.subcategoria.controllers.requests;

public record SubCategoriaRequest(String nome,
                                  Boolean ativo,
                                  Long categoriaId) {
}
