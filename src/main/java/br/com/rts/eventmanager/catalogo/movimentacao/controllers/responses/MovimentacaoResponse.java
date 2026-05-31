package br.com.rts.eventmanager.catalogo.movimentacao.controllers.responses;

import br.com.rts.eventmanager.catalogo.estoque.controllers.responses.EstoqueResponse;
import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.MotivoMovimentacaoEnum;
import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.TipoMovimentacaoEnum;
import br.com.rts.eventmanager.catalogo.produto.controllers.responses.ProdutoResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoResponse(Long id,
                                   UUID uuid,
                                   Long instituicao,
                                   Long evento,
                                   EstoqueResponse estoque,
                                   ProdutoResponse produto,
                                   TipoMovimentacaoEnum tipoMovimentacao,
                                   MotivoMovimentacaoEnum motivoMovimentacao,
                                   Integer quantidade,
                                   BigDecimal valorUnitario,
                                   LocalDateTime dataMovimentacao) {

}
