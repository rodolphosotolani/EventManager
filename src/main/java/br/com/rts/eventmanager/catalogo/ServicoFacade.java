package br.com.rts.eventmanager.catalogo;

import br.com.rts.eventmanager.catalogo.servico.mappers.ServicoMapper;
import br.com.rts.eventmanager.catalogo.servico.services.ServicoService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServicoFacade {

    private final ServicoService service;
    private final ServicoMapper mapper;

    public ServicoDTO findByIdAndInstituicaoAndEvento(@NotNull final Long servicoId,
                                                      @NotNull final Long instituicaoId,
                                                      @NotNull final Long eventoId) {

        return mapper.entityToDTO(
                service.findByInstituicaoAndEvento(servicoId, instituicaoId, eventoId));
    }

    public List<ServicoDTO> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                          @NotNull final Long eventoId) {

        return service.findAllByInstituicaoAndEvento(instituicaoId, eventoId)
                .stream()
                .map(mapper::entityToDTO)
                .toList();
    }
}
