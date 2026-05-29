package br.com.rts.eventmanager.catalogo.movimentacao.dtos;

import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.MotivoMovimentacaoEnum;
import br.com.rts.eventmanager.catalogo.movimentacao.enumerators.TipoMovimentacaoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class MovimentacaoDTO {

    private Long id;

    private UUID uuid;

    private Long instituicao;

    private Long evento;

    @NotNull
    private Integer quantidade;

    @Digits(integer = 10, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "22.08")
    private BigDecimal valorUnitario;

    @NotNull
    private Long produtoId;

    private String produtoNome;

    private TipoMovimentacaoEnum tipoMovimentacao;

    private MotivoMovimentacaoEnum motivoMovimentacao;

    private LocalDateTime dataMovimentacao;

    private Long categoriaId;

    private String categoriaNome;


    private Long subCategoriaId;

    private String subCategoriaNome;
}
