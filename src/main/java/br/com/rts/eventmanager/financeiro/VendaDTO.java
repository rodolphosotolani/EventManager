package br.com.rts.eventmanager.financeiro;

import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class VendaDTO {

    private Long id;

    @Digits(integer = 10, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "74.08")
    private BigDecimal valorTotal;

    private Integer quantidadeItens;

    private List<ItemVendaDTO> itens = new ArrayList<>();

    @NotNull
    private Boolean vendido;

    private FormaPagamentoEnum formaPagamento;

    private LocalDateTime dataVenda;

    private Long instituicao;

    private Long evento;

}
