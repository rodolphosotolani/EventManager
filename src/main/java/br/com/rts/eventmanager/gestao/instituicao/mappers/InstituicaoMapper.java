package br.com.rts.eventmanager.gestao.instituicao.mappers;

import br.com.rts.eventmanager.data.InstituicaoDTO;
import br.com.rts.eventmanager.gestao.instituicao.controllers.requests.InstituicaoRequest;
import br.com.rts.eventmanager.gestao.instituicao.controllers.responses.InstituicaoResponse;
import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class InstituicaoMapper {


    abstract public InstituicaoResponse entityToResponse(Instituicao instituicao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    abstract public Instituicao requestToEntity(InstituicaoRequest request);

    abstract public InstituicaoDTO entityToDTO(Instituicao instituicao);
}
