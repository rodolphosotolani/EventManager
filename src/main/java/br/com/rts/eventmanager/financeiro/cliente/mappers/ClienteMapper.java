package br.com.rts.eventmanager.financeiro.cliente.mappers;

import br.com.rts.eventmanager.financeiro.cliente.controllers.requests.ClienteRequest;
import br.com.rts.eventmanager.financeiro.cliente.controllers.responses.ClienteResponse;
import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ClienteMapper {

    public abstract ClienteResponse entityToResponse(Cliente cliente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    public abstract Cliente requestToEntity(ClienteRequest request);

    public abstract List<ClienteResponse> entityToResponse(List<Cliente> all);
}
