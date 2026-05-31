package br.com.rts.eventmanager.catalogo.movimentacao.mappers;

import br.com.rts.eventmanager.catalogo.movimentacao.controllers.requests.MovimentacaoRequest;
import br.com.rts.eventmanager.catalogo.movimentacao.controllers.responses.MovimentacaoResponse;
import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public abstract class MovimentacaoMapper {

    abstract public MovimentacaoResponse entityToResponse(Movimentacao movimentacao);

    public abstract Movimentacao requestToEntity(MovimentacaoRequest request, Long instituicaoId, Long eventoId);

    public abstract Movimentacao updateEntity(@MappingTarget Movimentacao movimentacaoExistente, MovimentacaoRequest request);
}
