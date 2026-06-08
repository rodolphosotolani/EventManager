package br.com.rts.eventmanager.catalogo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoDTO {

    private Long id;

    private UUID uuid;

    private Long instituicao;

    private Long evento;

    private String nome;

    private BigDecimal valorVendaUnitario;

    private CategoriaDTO categoria;

    private SubCategoriaDTO subCategoria;

}
