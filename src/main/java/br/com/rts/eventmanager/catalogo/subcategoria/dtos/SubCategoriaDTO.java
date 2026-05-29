package br.com.rts.eventmanager.catalogo.subcategoria.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoriaDTO {

    private Long id;

    private Long instituicao;

    @Size(max = 100)
    private String nome;

    @NotNull
    private Long categoriaId;

    private String categoriaNome;

    @Override
    public String toString() {
        return nome;
    }
}
