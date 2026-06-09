package br.com.rts.eventmanager.catalogo.produto.mappers;

import br.com.rts.eventmanager.catalogo.ProdutoDTO;
import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.produto.controllers.requests.ProdutoRequest;
import br.com.rts.eventmanager.catalogo.produto.controllers.responses.ProdutoResponse;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {CategoriaMapper.class, SubCategoriaMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProdutoMapper {

    abstract public ProdutoResponse entityToResponse(Produto produto);

    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "subCategoria.id", source = "subCategoriaId")
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    abstract public Produto requestToEntity(ProdutoRequest request);

    public abstract ProdutoDTO entityToDTO(Produto produto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "evento", ignore = true)
    public abstract Produto dtoToEntity(ProdutoDTO produtoDTO);

}
