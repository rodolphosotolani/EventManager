package br.com.rts.eventmanager.catalogo.movimentacao.mappers;

import br.com.rts.eventmanager.catalogo.movimentacao.controllers.requests.MovimentacaoRequest;
import br.com.rts.eventmanager.catalogo.movimentacao.controllers.responses.MovimentacaoResponse;
import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MovimentacaoMapper {

    abstract public MovimentacaoResponse entityToResponse(Movimentacao movimentacao);

    @Mapping(target = "estoque.id", source = "estoqueId")
    @Mapping(target = "produto.id", source = "produtoId")
    @Mapping(target = "dataMovimentacao", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    public abstract Movimentacao requestToEntity(MovimentacaoRequest request);

}
