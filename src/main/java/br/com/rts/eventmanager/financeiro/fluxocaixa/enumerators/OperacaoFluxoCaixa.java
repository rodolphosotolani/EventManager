package br.com.rts.eventmanager.financeiro.fluxocaixa.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperacaoFluxoCaixa {

    ENTRADA("Entradas (Recebimentos)"),
    SAIDA("Saídas (Pagamentos)"),
    MOVIMENTACAO("Movimentações Internas (Transferências)");

    private final String descricao;

}
