package br.com.rts.eventmanager.catalogo.movimentacao.mappers;

import br.com.rts.eventmanager.catalogo.movimentacao.controllers.requests.MovimentacaoRequest;
import br.com.rts.eventmanager.catalogo.movimentacao.controllers.responses.MovimentacaoResponse;
import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public abstract class MovimentacaoMapper {

    abstract public MovimentacaoResponse entityToResponse(Movimentacao movimentacao);

    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    public abstract Movimentacao requestToEntity(MovimentacaoRequest request);

}
