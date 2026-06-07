package br.com.rts.eventmanager.catalogo.servico.mappers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.servico.controllers.requests.ServicoRequest;
import br.com.rts.eventmanager.catalogo.servico.controllers.responses.ServicoResponse;
import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {CategoriaMapper.class, SubCategoriaMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ServicoMapper {

    abstract public ServicoResponse entityToResponse(Servico servico);

    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "subCategoria.id", source = "subCategoriaId")
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    public abstract Servico requestToEntity(ServicoRequest request);

}
