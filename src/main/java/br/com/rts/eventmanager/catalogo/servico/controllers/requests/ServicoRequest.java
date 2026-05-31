package br.com.rts.eventmanager.catalogo.servico.controllers.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServicoRequest(
        @NotNull Long evento,
        String nome,
        BigDecimal valorVenda,
        Long categoriaId,
        Long subCategoriaId) {
}
