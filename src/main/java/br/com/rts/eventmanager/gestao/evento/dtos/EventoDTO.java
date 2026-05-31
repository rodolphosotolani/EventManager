package br.com.rts.eventmanager.gestao.evento.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoDTO {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String nome;

    private Boolean ativo;

    private Long instituicaoId;

    @Override
    public String toString() {
        return nome;
    }
}
