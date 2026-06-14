package br.com.rts.eventmanager.financeiro.venda.controllers;

import br.com.rts.eventmanager.financeiro.venda.controllers.requests.VendaRequest;
import br.com.rts.eventmanager.financeiro.venda.controllers.responses.VendaResponse;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.mappers.VendaMapper;
import br.com.rts.eventmanager.financeiro.venda.services.VendaService;
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
@RestController("Vendas")
@RequestMapping("/api/v1/vendas")
@Tag(name = "Vendas", description = "API para gerenciamento de vendas")
public class VendaRestController {

    private final VendaService service;
    private final VendaMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar todas as vendas por instituição e evento",
            description = "Retorna uma página contendo todas as vendas vinculadas à instituição no cabeçalho e ao evento no path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendas retornadas com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma venda cadastrada para esta instituição neste evento")
    })
    public ResponseEntity<Page<VendaResponse>> getAllVendas(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<Venda> vendas = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        if (vendas == null || vendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(vendas.map(mapper::entityToResponse));
    }

    @GetMapping("/{vendaId}")
    @Operation(summary = "Obter detalhes de uma venda específica",
            description = "Retorna os detalhes de um único registro de venda pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada para esta instituição")
    })
    public ResponseEntity<VendaResponse> getVenda(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da venda", required = true)
            @PathVariable Long vendaId) {

        Optional<Venda> vendaOptional = service.findByIdAndInstituicao(vendaId, instituicaoId);

        return vendaOptional
                .map(venda -> ResponseEntity.ok(mapper.entityToResponse(venda)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/eventos/{eventoId}")
    @Operation(summary = "Cadastrar uma nova venda (Evento informado no payload)",
            description = "Cadastra uma nova venda. O cabeçalho deve conter o ID da instituição e os dados de venda (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venda criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<VendaResponse> create(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "Dados para criação da venda com evento no payload", required = true)
            @RequestBody VendaRequest request) {

        if (request.evento() == null) {
            return ResponseEntity.badRequest().build();
        }

        Venda venda = mapper.requestToEntity(request);
        venda.setInstituicao(instituicaoId);

        try {
            Venda vendaCriada = service.create(venda, instituicaoId, eventoId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(vendaCriada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{vendaId}")
    @Operation(summary = "Atualizar uma venda existente",
            description = "Atualiza os dados de uma venda pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<VendaResponse> update(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da venda a ser atualizada", required = true)
            @PathVariable Long vendaId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody VendaRequest request) {


        Venda vendaUpdate = mapper.requestToEntity(request);

        Venda vendaAtualizada = service.update(vendaId, vendaUpdate, instituicaoId);

        return ResponseEntity.ok(mapper.entityToResponse(vendaAtualizada));
    }

    @DeleteMapping("/eventos/{eventoId}/{vendaId}")
    @Operation(summary = "Excluir uma venda existente",
            description = "Remove definitivamente uma venda pelo seu ID se ela pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venda deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Venda não localizada ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID da venda a ser deletada", required = true)
            @PathVariable Long vendaId) {

        Optional<Venda> optionalVenda = service.findByIdAndInstituicaoAndEvento(vendaId, instituicaoId, eventoId);
        if (optionalVenda.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(vendaId);
        return ResponseEntity.noContent().build();
    }
}
