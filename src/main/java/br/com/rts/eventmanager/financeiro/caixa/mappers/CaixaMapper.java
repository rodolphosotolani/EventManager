package br.com.rts.eventmanager.financeiro.caixa.mappers;

import br.com.rts.eventmanager.financeiro.caixa.controllers.requests.CaixaRequest;
import br.com.rts.eventmanager.financeiro.caixa.controllers.responses.CaixaResponse;
import br.com.rts.eventmanager.financeiro.caixa.entities.Caixa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CaixaMapper {

    CaixaResponse entityToResponse(Caixa caixa);

    List<CaixaResponse> entityToResponse(List<Caixa> caixas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "fluxoCaixas", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    Caixa requestToEntity(CaixaRequest request);
}
