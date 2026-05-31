package br.com.rts.eventmanager.financeiro.cliente.controllers.requests;

public record ClienteRequest(String nome,
                             String apelido,
                             String celular,
                             String telefone,
                             String email) {
}
