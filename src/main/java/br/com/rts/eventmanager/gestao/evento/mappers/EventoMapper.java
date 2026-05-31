package br.com.rts.eventmanager.gestao.evento.mappers;

import br.com.rts.eventmanager.gestao.evento.controllers.requests.EventoRequest;
import br.com.rts.eventmanager.gestao.evento.controllers.responses.EventoResponse;
import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EventoMapper {

    @Mapping(target = "instituicaoId", source = "instituicao.id")
    public abstract EventoResponse entityToResponse(Evento evento);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    public abstract Evento requestToEntity(EventoRequest request);

    public abstract List<EventoResponse> entityToResponse(List<Evento> all);
}
