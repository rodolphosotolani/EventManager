package br.com.rts.eventmanager.catalogo.servico.controllers.requests;

import java.math.BigDecimal;

public record ServicoRequest(String nome,
                             BigDecimal valorVenda,
                             Long categoriaId,
                             Long subCategoriaId) {
}
