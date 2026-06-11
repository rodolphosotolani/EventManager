package br.com.rts.eventmanager.financeiro;

import br.com.rts.eventmanager.catalogo.ProdutoDTO;
import br.com.rts.eventmanager.catalogo.ServicoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaDTO {

    private Long id;
    private ProdutoDTO produto;
    private ServicoDTO servico;
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