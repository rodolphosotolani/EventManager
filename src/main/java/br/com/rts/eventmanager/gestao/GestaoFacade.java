package br.com.rts.eventmanager.gestao;

import br.com.rts.eventmanager.gestao.evento.services.EventoService;
import br.com.rts.eventmanager.gestao.instituicao.services.InstituicaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GestaoFacade {

    private final InstituicaoService instituicaoService;

    private final EventoService eventoService;

    public void validateIfInstituicaoIsValid(Long instituicaoId) {

        Boolean instituicaoExists = instituicaoService.existsById(instituicaoId);

        if (!instituicaoExists)
            throw new NotFoundException("Instituição não encontrada!");
    }

    public void validateIfInstituicaoAndEventoIsValid(Long instituicaoId, Long eventoId) {

        this.validateIfInstituicaoIsValid(instituicaoId);

        Boolean eventoExists = eventoService.existsByInstituicaoAndId(instituicaoId, eventoId);

        if (!eventoExists)
            throw new NotFoundException("Evento não encontrado!");
    }
}
