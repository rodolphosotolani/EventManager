package br.com.rts.eventmanager.financeiro.venda.enumerators;


import lombok.Getter;

@Getter
public enum FormaPagamentoEnum {

    DINHEIRO("Dinheiro"),
    PIX("PIX"),
    CARTAO_DEBITO("Cartão de Débito"),
    CARTAO_CREDITO("Cartão de Crédito"),
    ANOTAR_CONTA("Anotar na Conta");

    private final String descricao;

    FormaPagamentoEnum(final String descricao) {
        this.descricao = descricao;
    }

}
