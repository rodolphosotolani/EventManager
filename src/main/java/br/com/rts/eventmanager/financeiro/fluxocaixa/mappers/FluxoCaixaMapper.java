package br.com.rts.eventmanager.financeiro.fluxocaixa.mappers;

import br.com.rts.eventmanager.financeiro.fluxocaixa.controllers.requests.FluxoCaixaRequest;
import br.com.rts.eventmanager.financeiro.fluxocaixa.controllers.responses.FluxoCaixaResponse;
import br.com.rts.eventmanager.financeiro.fluxocaixa.entities.FluxoCaixa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FluxoCaixaMapper {

    @Mapping(target = "caixaId", source = "caixa.id")
    @Mapping(target = "vendaId", source = "venda.id")
    FluxoCaixaResponse entityToResponse(FluxoCaixa fluxoCaixa);

    List<FluxoCaixaResponse> entityToResponse(List<FluxoCaixa> fluxoCaixas);

    @Mapping(target = "caixa.id", source = "caixaId")
    @Mapping(target = "venda.id", source = "vendaId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    FluxoCaixa requestToEntity(FluxoCaixaRequest request);
}
