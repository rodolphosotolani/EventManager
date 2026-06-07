package br.com.rts.eventmanager.catalogo.categoria.mappers;

import br.com.rts.eventmanager.catalogo.categoria.controllers.requests.CategoriaRequest;
import br.com.rts.eventmanager.catalogo.categoria.controllers.responses.CategoriaResponse;
import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {SubCategoriaMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CategoriaMapper {

    abstract public CategoriaResponse entityToResponse(Categoria categoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subCategorias", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    abstract public Categoria requestToEntity(CategoriaRequest request);

}
