package br.com.rts.eventmanager.catalogo.servico.controllers.responses;

import br.com.rts.eventmanager.catalogo.categoria.controllers.responses.CategoriaResponse;
import br.com.rts.eventmanager.catalogo.subcategoria.controllers.SubCategoriaResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoResponse(Long id,
                              UUID uuid,
                              Long instituicao,
                              Long evento,
                              String nome,
                              BigDecimal valorVenda,
                              CategoriaResponse categoria,
                              SubCategoriaResponse subCategoria) {

}
