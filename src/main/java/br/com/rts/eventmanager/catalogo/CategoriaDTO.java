package br.com.rts.eventmanager.catalogo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Long id;

    private Long instituicao;

    private String nome;

    private Boolean ativo;

}
