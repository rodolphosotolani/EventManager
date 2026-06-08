package br.com.rts.eventmanager.financeiro.venda.mappers;

import br.com.rts.eventmanager.financeiro.itemvenda.mappers.ItemVendaMapper;
import br.com.rts.eventmanager.financeiro.venda.controllers.requests.VendaRequest;
import br.com.rts.eventmanager.financeiro.venda.controllers.responses.VendaResponse;
import br.com.rts.eventmanager.financeiro.VendaDTO;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemVendaMapper.class})
public interface VendaMapper {

    @Mapping(target = "quantidadeItens", expression = "java(venda.getItens() != null ? venda.getItens().size() : 0)")
    VendaResponse entityToResponse(Venda venda);

    List<VendaResponse> entityToResponse(List<Venda> vendas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "itens", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    Venda requestToEntity(VendaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instituicao", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    Venda dtoToEntity(@Valid VendaDTO vendaDTO);
}
