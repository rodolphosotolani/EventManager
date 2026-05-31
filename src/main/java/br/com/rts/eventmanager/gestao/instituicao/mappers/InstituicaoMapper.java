package br.com.rts.eventmanager.gestao.instituicao.mappers;

import br.com.rts.eventmanager.gestao.instituicao.controllers.requests.InstituicaoRequest;
import br.com.rts.eventmanager.gestao.instituicao.controllers.responses.InstituicaoResponse;
import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class InstituicaoMapper {


    abstract public InstituicaoResponse entityToResponse(Instituicao instituicao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    abstract public Instituicao requestToEntity(InstituicaoRequest request);
}
