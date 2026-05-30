package br.com.rts.eventmanager.catalogo.categoria.mappers;

import br.com.rts.eventmanager.catalogo.categoria.controllers.requests.CategoriaRequest;
import br.com.rts.eventmanager.catalogo.categoria.controllers.responses.CategoriaResponse;
import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SubCategoriaMapper.class})
public abstract class CategoriaMapper {

    abstract public CategoriaResponse entityToResponse(Categoria categoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "subCategorias", ignore = true)
    abstract public Categoria requestToEntity(CategoriaRequest request);

}
