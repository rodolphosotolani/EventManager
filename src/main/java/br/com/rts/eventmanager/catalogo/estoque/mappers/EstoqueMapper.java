package br.com.rts.eventmanager.catalogo.estoque.mappers;

import br.com.rts.eventmanager.catalogo.estoque.controllers.requests.EstoqueRequest;
import br.com.rts.eventmanager.catalogo.estoque.controllers.responses.EstoqueResponse;
import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EstoqueMapper {

    abstract public EstoqueResponse entityToResponse(Estoque estoque);

    @Mapping(target = "produto.id", source = "produtoId")
    @Mapping(target = "quantidadeAtual", source = "quantidade")
    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    abstract public Estoque requestToEntity(EstoqueRequest request);

}
