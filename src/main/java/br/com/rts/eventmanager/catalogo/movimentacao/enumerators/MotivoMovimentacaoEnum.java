package br.com.rts.eventmanager.catalogo.movimentacao.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MotivoMovimentacaoEnum {

    VENDA("Venda"),
    PERDA("Perda"),
    AJUSTE("Ajuste"),
    COMPRA("Compra"),
    DOACAO("Doação"),
    DEVOLUCAO("Devolução");

    private final String descricao;
}
