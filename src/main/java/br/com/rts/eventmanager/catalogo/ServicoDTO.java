package br.com.rts.eventmanager.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoDTO {

    private Long id;

    private UUID uuid;

    private Long instituicao;

    private Long evento;

    private String nome;

    private BigDecimal valorVenda;

    private CategoriaDTO categoria;

    private SubCategoriaDTO subCategoria;

}
