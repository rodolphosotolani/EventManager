package br.com.rts.eventmanager.gestao;

import br.com.rts.eventmanager.gestao.evento.mappers.EventoMapper;
import br.com.rts.eventmanager.gestao.evento.services.EventoService;
import br.com.rts.eventmanager.gestao.instituicao.mappers.InstituicaoMapper;
import br.com.rts.eventmanager.gestao.instituicao.services.InstituicaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GestaoFacade {

    private final InstituicaoService instituicaoService;
    private final InstituicaoMapper instituicaoMapper;

    private final EventoService eventoService;
    private final EventoMapper eventoMapper;

    /* ####################################################
                            INSTITUICAO
     ##################################################### */
    public void validateIfInstituicaoIsValid(Long instituicaoId) {
        instituicaoService.validateIfIsValid(instituicaoId);
    }

    public InstituicaoDTO getInstituicaoById(Long instituicao) {
        return instituicaoMapper.entityToDTO(
                instituicaoService.findById(instituicao));
    }

    public Page<InstituicaoDTO> findAllInstituicao(Pageable pageable) {
        return instituicaoService.findAll(pageable)
                .map(instituicaoMapper::entityToDTO);
    }

    public List<InstituicaoDTO> findAllInstituicao() {
        return instituicaoService.findAll()
                .stream()
                .map(instituicaoMapper::entityToDTO)
                .toList();
    }

    /* ####################################################
                            EVENTO
     ##################################################### */
    public void validateIfInstituicaoAndEventoIsValid(Long instituicaoId, Long eventoId) {

        this.validateIfInstituicaoIsValid(instituicaoId);

        eventoService.validateIfEventoIsValid(instituicaoId, eventoId);
    }

    public EventoDTO getEventoById(Long instituicao, Long eventoId) {
        return eventoService.findByIdAndInstituicao(instituicao, eventoId)
                .map(eventoMapper::entityToDTO)
                .orElseThrow(() -> new NotFoundException("Instituição não encontrada!"));
    }

    public List<EventoDTO> findAllEventoByInstituicaoId(Long instituicaoId) {
        return eventoService.findAllByInstituicao(instituicaoId)
                .stream()
                .map(eventoMapper::entityToDTO)
                .toList();
    }
}
