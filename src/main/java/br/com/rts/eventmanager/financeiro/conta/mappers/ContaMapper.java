package br.com.rts.eventmanager.financeiro.conta.mappers;

import br.com.rts.eventmanager.financeiro.conta.controllers.requests.ContaRequest;
import br.com.rts.eventmanager.financeiro.conta.controllers.responses.ContaResponse;
import br.com.rts.eventmanager.financeiro.conta.entities.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ContaMapper {

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "vendaId", source = "venda.id")
    public abstract ContaResponse entityToResponse(Conta conta);

    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "venda.id", source = "vendaId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    public abstract Conta requestToEntity(ContaRequest request);

    public abstract List<ContaResponse> entityToResponse(List<Conta> all);
}
