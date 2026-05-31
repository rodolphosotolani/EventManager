package br.com.rts.eventmanager.financeiro.itemvenda.mappers;

import br.com.rts.eventmanager.financeiro.itemvenda.controllers.requests.ItemVendaRequest;
import br.com.rts.eventmanager.financeiro.itemvenda.controllers.responses.ItemVendaResponse;
import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ItemVendaMapper {

    abstract public @Nullable ItemVendaResponse entityToResponse(ItemVenda itemVenda);

    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    public abstract ItemVenda requestToEntity(ItemVendaRequest request);
}
