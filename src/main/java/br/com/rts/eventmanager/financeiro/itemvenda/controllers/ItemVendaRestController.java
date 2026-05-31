package br.com.rts.eventmanager.financeiro.itemvenda.controllers;

import br.com.rts.eventmanager.financeiro.itemvenda.controllers.requests.ItemVendaRequest;
import br.com.rts.eventmanager.financeiro.itemvenda.controllers.responses.ItemVendaResponse;
import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.itemvenda.mappers.ItemVendaMapper;
import br.com.rts.eventmanager.financeiro.itemvenda.services.ItemVendaService;
import br.com.rts.eventmanager.utils.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController("ItensVenda")
@RequestMapping("/api/v1/itens-venda")
@Tag(name = "ItensVenda", description = "API para gerenciamento de itens de venda")
public class ItemVendaRestController {

    private final ItemVendaService service;
    private final ItemVendaMapper mapper;

    @GetMapping("/eventos/{eventoId}/vendas/{vendaId}")
    @Operation(summary = "Listar todos os itens de uma venda por instituição e evento",
            description = "Retorna uma página contendo todos os itens vinculados à venda, instituição no cabeçalho e ao evento no path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens de venda retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum item cadastrado para esta venda nesta instituição neste evento")
    })
    public ResponseEntity<Page<ItemVendaResponse>> getAllItemVendas(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID da venda", required = true)
            @PathVariable Long vendaId,
            Pageable pageable) {

        Page<ItemVenda> itemVendas = service.findAllByInstituicaoAndEventoAndVendaId(instituicaoId, eventoId, vendaId, pageable);

        if (itemVendas == null || itemVendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(itemVendas.map(mapper::entityToResponse));
    }

    @GetMapping("/{itemVendaId}")
    @Operation(summary = "Obter detalhes de um item de venda",
            description = "Retorna os detalhes de um único item de venda pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item de venda localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item de venda não encontrado ou não pertence a esta instituição")
    })
    public ResponseEntity<ItemVendaResponse> getItemVenda(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do item de venda", required = true)
            @PathVariable Long itemVendaId) {

        try {
            ItemVenda itemVenda = service.get(itemVendaId);
            if (!itemVenda.getInstituicao().equals(instituicaoId)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(mapper.entityToResponse(itemVenda));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    @Operation(summary = "Cadastrar um novo item de venda (Evento no payload)",
            description = "Cadastra um novo item de venda. O cabeçalho deve conter o ID da instituição e o corpo deve informar o ID do evento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item de venda criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou evento não informado")
    })
    public ResponseEntity<ItemVendaResponse> create(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados para criação do item com evento no payload", required = true)
            @RequestBody ItemVendaRequest request) {

        ItemVenda itemVenda = mapper.requestToEntity(request);

        try {
            ItemVenda itemVendaCriada = service.create(itemVenda, instituicaoId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(itemVendaCriada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{itemVendaId}")
    @Operation(summary = "Atualizar um item de venda existente",
            description = "Atualiza os dados de um item pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item de venda atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item de venda não encontrado nesta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ItemVendaResponse> update(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do item de venda", required = true)
            @PathVariable Long itemVendaId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody ItemVendaRequest request) {

        ItemVenda itemUpdate = mapper.requestToEntity(request);

        try {

            ItemVenda itemAtualizado = service.update(itemVendaId, itemUpdate, instituicaoId);

            return ResponseEntity.ok(mapper.entityToResponse(itemAtualizado));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eventos/{eventoId}/{itemVendaId}")
    @Operation(summary = "Excluir um item de venda existente",
            description = "Remove definitivamente um item de venda pelo seu ID se pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item de venda deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item de venda não localizado nesta instituição neste evento")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do item de venda a ser deletado", required = true)
            @PathVariable Long itemVendaId) {

        Optional<ItemVenda> optionalItem = service.findByIdAndInstituicaoAndEvento(itemVendaId, instituicaoId, eventoId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(itemVendaId);
        return ResponseEntity.noContent().build();
    }
}
