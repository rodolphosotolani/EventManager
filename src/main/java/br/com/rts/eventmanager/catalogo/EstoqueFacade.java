package br.com.rts.eventmanager.catalogo;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstoqueFacade {

    private final EstoqueService service;

    public Page<Estoque> findAllByInstituicaoAndEvento(@NotNull final Long instituicaoId,
                                                @NotNull final Long eventoId,
                                                Pageable pageable){
        return service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    public Estoque findByIdAndInstituicaoAndEvento(@NotNull final Long estoqueId,
                                            @NotNull final Long instituicaoId,
                                            @NotNull final Long eventoId){

        return service.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, eventoId);
    }

    public Estoque create(final Estoque estoque,
                   @NotNull final Long instituicaoId){

        return service.create(estoque, instituicaoId);
    }

    public void delete(@NotNull final Long estoqueId,
                @NotNull final Long instituicaoId,
                @NotNull final Long eventoId){

        service.delete(estoqueId, instituicaoId, eventoId);
    }

//    Estoque adicionaAoEstoque(@NotNull final Long estoqueId,
//                              final Estoque estoqueNew,
//                              @NotNull final Long instituicaoId);

    public Estoque updateEstoque(@NotNull final Long estoqueId,
                          final Estoque estoqueUpdate,
                          @NotNull final Long instituicaoId){

        return service.updateEstoque(estoqueId, estoqueUpdate, instituicaoId);
    }

    public void subtrairEstoqueProduto(Long produtoId, Long instituicaoId, Long eventoId, int quantidade){

        service.subtrairEstoqueProduto(produtoId, instituicaoId, eventoId, quantidade);
    }

}
