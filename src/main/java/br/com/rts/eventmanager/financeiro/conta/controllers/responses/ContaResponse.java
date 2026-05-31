package br.com.rts.eventmanager.financeiro.conta.controllers.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContaResponse(Long id,
                            UUID uuid,
                            Long instituicao,
                            Long evento,
                            Long clienteId,
                            String clienteNome,
                            Long vendaId,
                            BigDecimal saldoDevedor,
                            Boolean pago,
                            LocalDateTime dataPagamento) {
}
