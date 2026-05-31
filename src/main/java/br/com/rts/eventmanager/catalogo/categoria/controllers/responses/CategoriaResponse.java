package br.com.rts.eventmanager.catalogo.categoria.controllers.responses;

public record CategoriaResponse(Long id,
                                Long instituicao,
                                String nome,
                                Boolean ativo) {
}
