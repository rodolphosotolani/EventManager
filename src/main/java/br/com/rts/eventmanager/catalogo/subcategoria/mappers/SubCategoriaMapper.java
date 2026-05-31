package br.com.rts.eventmanager.catalogo.subcategoria.mappers;

import br.com.rts.eventmanager.catalogo.subcategoria.controllers.requests.SubCategoriaRequest;
import br.com.rts.eventmanager.catalogo.subcategoria.controllers.responses.SubCategoriaResponse;
import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SubCategoriaMapper {

    abstract public SubCategoriaResponse entityToResponse(SubCategoria subCategoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    abstract public SubCategoria requestToEntity(SubCategoriaRequest request);

}
