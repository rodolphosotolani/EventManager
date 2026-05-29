package br.com.rts.eventmanager.catalogo.categoria.mappers;

import br.com.rts.eventmanager.catalogo.categoria.dtos.CategoriaDTO;
import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SubCategoriaMapper.class})
public abstract class CategoriaMapper {

    @Autowired
    CategoriaService service;

    @Mapping(target = "subcategorias", source = "id", qualifiedByName = "getSubcategoriasByCategoriaId")
    abstract public CategoriaDTO entityToDTO(Categoria categoria);

    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "subCategorias", ignore = true)
    abstract public Categoria dtoToEntity(CategoriaDTO categoriaDTO);

    public Categoria idToEntity(Long categoriaId) {
        return service.findById(categoriaId);
    }

    abstract public @Nullable List<CategoriaDTO> entityToDTO(List<Categoria> categoriaList);
}
