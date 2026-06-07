package br.com.rts.eventmanager.data;

public record EventoDTO(Long id,
                        String nome,
                        Long instituicaoId,
                        Boolean ativo) {
}
