package br.com.rts.eventmanager.financeiro.caixa.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCaixaEnum {

    ABERTO("Aberto"),
    FECHADO("Fechado");

    private final String descricao;
}
