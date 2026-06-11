package br.com.rts.eventmanager.financeiro.itemvenda.mappers;

import br.com.rts.eventmanager.financeiro.itemvenda.controllers.requests.ItemVendaRequest;
import br.com.rts.eventmanager.financeiro.itemvenda.controllers.responses.ItemVendaResponse;
import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.ItemVendaDTO;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ItemVendaMapper {

    @Mapping(target = "produtoId", source = "produto")
    @Mapping(target = "servicoId", source = "servico")
    @Mapping(target = "vendaId", source = "venda.id")
    abstract public @Nullable ItemVendaResponse entityToResponse(ItemVenda itemVenda);

    @Mapping(target = "produto", source = "produtoId")
    @Mapping(target = "servico", source = "servicoId")
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    public abstract ItemVenda requestToEntity(ItemVendaRequest request);

    @Mapping(target = "produto", source = "produto.id")
    @Mapping(target = "servico", source = "servico.id")
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    public abstract ItemVenda dtoToEntity(ItemVendaDTO itemVendaDTO);

    @Mapping(target = "produto.id", source = "produto")
    @Mapping(target = "servico.id", source = "servico")
    public abstract ItemVendaDTO entityToDTO(ItemVenda itemVenda);

}
