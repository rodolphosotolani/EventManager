package br.com.rts.eventmanager.catalogo;

import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProdutoFacade {

    private final ProdutoService service;
    private final ProdutoMapper mapper;

    public ProdutoDTO findByIdAndInstituicaoAndEvento(@NotNull final Long produtoId,
                                                      @NotNull final Long instituicaoId,
                                                      @NotNull final Long eventoId) {

        return mapper.entityToDTO(
                service.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId));
    }


    public List<ProdutoDTO> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                          @NotNull final Long eventoId) {

        return service.findAllByInstituicaoAndEvento(instituicaoId, eventoId)
                .stream()
                .map(mapper::entityToDTO)
                .toList();
    }
}
