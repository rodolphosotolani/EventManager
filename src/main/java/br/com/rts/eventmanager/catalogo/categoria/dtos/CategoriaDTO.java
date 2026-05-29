package br.com.rts.eventmanager.catalogo.categoria.dtos;

import br.com.rts.eventmanager.catalogo.subcategoria.dtos.SubCategoriaDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class CategoriaDTO {

    private Long id;

    private Long instituicao;

    @NotNull
    @Size(max = 100)
    private String nome;

    private Boolean ativo;

    private List<SubCategoriaDTO> subcategorias = new ArrayList<>();

    @Override
    public String toString() {
        return nome;
    }
}
