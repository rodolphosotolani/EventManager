package br.com.rts.eventmanager.financeiro.venda.controllers.responses;

import br.com.rts.eventmanager.financeiro.itemvenda.controllers.responses.ItemVendaResponse;
import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponse(Long id,
                            Long instituicao,
                            Long evento,
                            BigDecimal valorTotal,
                            Integer quantidadeItens,
                            List<ItemVendaResponse> itens,
                            Boolean vendido,
                            FormaPagamentoEnum formaPagamento,
                            LocalDateTime dataVenda) {

}
