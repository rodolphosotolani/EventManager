package br.com.rts.eventmanager.financeiro.fluxocaixa.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static br.com.rts.eventmanager.financeiro.fluxocaixa.enumerators.OperacaoFluxoCaixa.*;

@Getter
@RequiredArgsConstructor
public enum TipoFluxoCaixaEnum {

    ABERTO("Caixa Aberto", MOVIMENTACAO),
    VENDA("Venda", ENTRADA),
    SANGRIA("Sangria de Caixa", MOVIMENTACAO),
    SUPRIMENTO("Suprimento de caixa", MOVIMENTACAO),
    FECHAMENTO("Fechamento de Caixa", MOVIMENTACAO),
    ESTORNO("Estorno de valores", SAIDA),
    RECEBIMENTO_CONTA("Recebimento de Conta", ENTRADA);

    private final String descricao;
    private final OperacaoFluxoCaixa operacao;


}
