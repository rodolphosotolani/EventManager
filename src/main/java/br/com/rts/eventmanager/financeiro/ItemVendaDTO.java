package br.com.rts.eventmanager.financeiro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaDTO {

    private Long id;
    private Long produtoId;
    private String produtoNome;
    private BigDecimal produtoValorUnitario;
    private String categoriaNome;
    private String subCategoriaNome;
    private Integer quantidade;
    private Long instituicao;
    private Long evento;


    public void adicionarQuantidade(Integer quantidade) {
        this.quantidade += quantidade;
    }

    public void subtrairQuantidade(Integer quantidade) {
        this.quantidade -= quantidade;
    }
}