package br.com.rts.eventmanager.financeiro.venda.services;

import br.com.rts.eventmanager.financeiro.venda.entities.Venda;

public interface PrinterService {

    byte[] generateEscPosBytes(Venda venda);
}
