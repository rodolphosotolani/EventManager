package br.com.rts.eventmanager.catalogo.produto.mappers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.produto.controllers.requests.ProdutoRequest;
import br.com.rts.eventmanager.catalogo.produto.controllers.responses.ProdutoResponse;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {CategoriaMapper.class, SubCategoriaMapper.class})
public abstract class ProdutoMapper {

    abstract public ProdutoResponse entityToResponse(Produto produto);

    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "subCategoria.id", source = "subCategoriaId")
    abstract public Produto requestToEntity(ProdutoRequest request);
}
