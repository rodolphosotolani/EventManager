package br.com.rts.eventmanager.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    private Long id;

    private UUID uuid;

    private Long instituicao;

    private Long evento;

    private String nome;

    private String especificacao;

    private BigDecimal valorVendaUnitario;

    private Integer quantidadeMinima;

    private CategoriaDTO categoria;

    private SubCategoriaDTO subCategoria;

}
