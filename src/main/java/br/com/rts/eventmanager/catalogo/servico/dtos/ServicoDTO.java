package br.com.rts.eventmanager.catalogo.servico.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ServicoDTO {

    private Long id;

    private UUID uuid;

    private Long instituicao;

    private Long evento;

    @NotNull
    @Size(max = 100)
    private String nome;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "56.08")
    private BigDecimal valorVenda;

    @NotNull
    private Long categoriaId;

    private String categoriaNome;

    private Long subCategoriaId;

    private String subCategoriaNome;

    @Override
    public String toString() {
        return nome;
    }
}
