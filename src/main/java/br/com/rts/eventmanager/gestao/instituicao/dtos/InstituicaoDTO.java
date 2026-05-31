package br.com.rts.eventmanager.gestao.instituicao.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InstituicaoDTO {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String nome;

    private Boolean ativo;

    @Override
    public String toString() {
        return nome;
    }
}
