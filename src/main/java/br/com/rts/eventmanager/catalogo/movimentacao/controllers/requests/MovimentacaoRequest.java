package br.com.rts.eventmanager.catalogo.movimentacao.controllers.requests;

import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.MotivoMovimentacaoEnum;
import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.TipoMovimentacaoEnum;

import java.math.BigDecimal;

public record MovimentacaoRequest(Long estoqueId,
                                  Long produtoId,
                                  TipoMovimentacaoEnum tipoMovimentacao,
                                  MotivoMovimentacaoEnum motivoMovimentacao,
                                  Integer quantidade,
                                  BigDecimal valorUnitario) {

}
