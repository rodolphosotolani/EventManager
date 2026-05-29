package br.com.rts.eventmanager.catalogo.servico.mappers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.servico.dtos.ServicoDTO;
import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import br.com.rts.eventmanager.catalogo.servico.services.ServicoService;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoriaMapper.class, SubCategoriaMapper.class})
public abstract class ServicoMapper {

    @Autowired
    ServicoService servicoService;

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNome", source = "categoria.nome")
    @Mapping(target = "subCategoriaId", source = "subCategoria.id")
    @Mapping(target = "subCategoriaNome", source = "subCategoria.nome")
    abstract public ServicoDTO entityToDTO(Servico Servico);

    @Mapping(target = "uuid", defaultExpression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "categoria", source = "categoriaId")
    @Mapping(target = "subCategoria", source = "subCategoriaId")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    abstract public Servico dtoToEntity(ServicoDTO servicoDTO);

    public Servico idToEntity(Long ServicoId) {
        return servicoService.findById(ServicoId);
    }

    public abstract List<ServicoDTO> entityToDTO(List<Servico> all);
}
