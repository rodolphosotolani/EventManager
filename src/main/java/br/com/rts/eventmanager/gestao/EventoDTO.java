package br.com.rts.eventmanager.gestao;

public record EventoDTO(Long id,
                        String nome,
                        Long instituicaoId,
                        Boolean ativo) {
}
