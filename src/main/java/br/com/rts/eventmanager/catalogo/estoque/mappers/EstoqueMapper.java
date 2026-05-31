package br.com.rts.eventmanager.catalogo.estoque.mappers;

import br.com.rts.eventmanager.catalogo.estoque.controllers.requests.EstoqueRequest;
import br.com.rts.eventmanager.catalogo.estoque.controllers.responses.EstoqueResponse;
import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public abstract class EstoqueMapper {

    abstract public EstoqueResponse entityToResponse(Estoque estoque);

    abstract public Estoque requestToEntity(EstoqueRequest request);

}
