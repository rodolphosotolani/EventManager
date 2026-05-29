package br.com.rts.eventmanager.catalogo.subcategoria.mappers;

import br.com.rts.eventmanager.catalogo.subcategoria.dtos.SubCategoriaDTO;
import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SubCategoriaMapper {

    @Autowired
    SubCategoriaService service;

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNome", source = "categoria.nome")
    abstract public SubCategoriaDTO entityToDTO(SubCategoria subCategoria);

    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    abstract public SubCategoria dtoToEntity(SubCategoriaDTO subCategoriaDTO);

    abstract public @Nullable List<SubCategoriaDTO> entityToDTO(List<SubCategoria> subCategoriaList);

    public SubCategoria idToEntity(Long subCategoriaId) {
        if (subCategoriaId != null)
            return service.findById(subCategoriaId);
        return null;
    }

    @Named("getSubcategoriasByCategoriaId")
    public List<SubCategoriaDTO> getSubcategoriasByCategoriaId(Long categoriaId) {
        if (categoriaId == null) return null;
        return this.entityToDTO(service.findAllByCategoria(categoriaId));
    }
}
