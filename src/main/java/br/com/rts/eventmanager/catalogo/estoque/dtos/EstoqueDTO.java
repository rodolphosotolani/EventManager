package br.com.rts.eventmanager.catalogo.estoque.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
public class EstoqueDTO {

    private Long id;

    private UUID uuid;

    private Long instituicao;

    private Long evento;

    @NotNull
    private Integer quantidadeAtual;

    private Integer quantidadeInicial;

    @Digits(integer = 10, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "22.08")
    private BigDecimal valorCompraUnitario;

    @NotNull
    private Long produtoId;

    private String produtoNome;


    private Long categoriaId;

    private String categoriaNome;


    private Long subCategoriaId;

    private String subCategoriaNome;
}
