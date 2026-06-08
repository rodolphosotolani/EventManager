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

    public InstituicaoDTO getInstituicaoById(Long instituicao) {
        return instituicaoService.findById(instituicao)
                .map(instituicaoMapper::entityToDTO)
                .orElseThrow(() -> new NotFoundException("Instituição não encontrada!"));
    }

    public EventoDTO getEventoById(Long instituicao, Long eventoId) {
        return eventoService.findByIdAndInstituicao(instituicao, eventoId)
                .map(eventoMapper::entityToDTO)
                .orElseThrow(() -> new NotFoundException("Instituição não encontrada!"));
    }

    public Page<InstituicaoDTO> findAllInstituicao(Pageable pageable) {
        return instituicaoService.findAll(pageable)
                .map(instituicaoMapper::entityToDTO);
    }

    public Optional<InstituicaoDTO> findInstituicaoById(Long instituicaoId) {
        return instituicaoService.findById(instituicaoId)
                .map(instituicaoMapper::entityToDTO);
    }

    public List<InstituicaoDTO> findAllInstituicao() {
        return instituicaoService.findAll()
                .stream()
                .map(instituicaoMapper::entityToDTO)
                .toList();
    }

    public boolean existsEventoByInstituicaoAndId(Long instituicaoId, Long eventoId) {
        return eventoService.existsByInstituicaoAndId(instituicaoId, eventoId);
    }

    public List<EventoDTO> findAllEventoByInstituicaoId(Long instituicaoId) {
        return eventoService.findAllByInstituicao(instituicaoId)
                .stream()
                .map(eventoMapper::entityToDTO)
                .toList();
    }
}
