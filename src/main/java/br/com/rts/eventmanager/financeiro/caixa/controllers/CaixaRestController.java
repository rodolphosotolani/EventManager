package br.com.rts.eventmanager.financeiro.caixa.controllers;

import br.com.rts.eventmanager.financeiro.caixa.controllers.requests.CaixaRequest;
import br.com.rts.eventmanager.financeiro.caixa.controllers.responses.CaixaResponse;
import br.com.rts.eventmanager.financeiro.caixa.entities.Caixa;
import br.com.rts.eventmanager.financeiro.caixa.mappers.CaixaMapper;
import br.com.rts.eventmanager.financeiro.caixa.services.CaixaService;
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
@RestController("Caixas")
@RequestMapping("/api/v1/caixas")
@Tag(name = "Caixas", description = "API para gerenciamento de caixas")
public class CaixaRestController {

    private final CaixaService service;
    private final CaixaMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar todos os caixas por instituição e evento", 
               description = "Retorna uma página contendo todos os caixas vinculados à instituição no cabeçalho e ao evento no path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caixas retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum caixa cadastrado para esta instituição neste evento")
    })
    public ResponseEntity<Page<CaixaResponse>> getAllCaixas(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<Caixa> caixas = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        if (caixas == null || caixas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(caixas.map(mapper::entityToResponse));
    }

    @GetMapping("/eventos/{eventoId}/{caixaId}")
    @Operation(summary = "Obter detalhes de um caixa específico", 
               description = "Retorna os detalhes de um único registro de caixa pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caixa localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Caixa não encontrado para esta instituição")
    })
    public ResponseEntity<CaixaResponse> getCaixaById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do registro de caixa", required = true)
            @PathVariable Long caixaId) {

        Optional<Caixa> caixaOptional = service.findByIdAndInstituicaoAndEvento(caixaId, instituicaoId, eventoId);

        return caixaOptional
                .map(caixa -> ResponseEntity.ok(mapper.entityToResponse(caixa)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Operation(summary = "Cadastrar um novo caixa (Evento informado no payload)", 
               description = "Cadastra um novo caixa. O cabeçalho deve conter o ID da instituição e os dados de caixa (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Caixa criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<CaixaResponse> create(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados para criação do caixa com evento no payload", required = true)
            @RequestBody CaixaRequest request) {

        Caixa caixa = mapper.requestToEntity(request);

        try {
            Caixa caixaCriado = service.create(caixa, instituicaoId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(caixaCriado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{caixaId}")
    @Operation(summary = "Atualizar um caixa existente", 
               description = "Atualiza os dados de um caixa pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caixa atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Caixa não encontrado para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<CaixaResponse> update(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do caixa a ser atualizado", required = true)
            @PathVariable Long caixaId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody CaixaRequest request) {

        Caixa caixaUpdate = mapper.requestToEntity(request);

        try {

            Caixa caixaAtualizado = service.update(caixaId, caixaUpdate, instituicaoId);

            return ResponseEntity.ok(mapper.entityToResponse(caixaAtualizado));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eventos/{eventoId}/{caixaId}")
    @Operation(summary = "Excluir um caixa existente", 
               description = "Remove definitivamente um caixa pelo seu ID se ele pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Caixa deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Caixa não localizado ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do caixa a ser deletado", required = true)
            @PathVariable Long caixaId) {

        Optional<Caixa> optionalCaixa = service.findByIdAndInstituicaoAndEvento(caixaId, instituicaoId, eventoId);
        if (optionalCaixa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(caixaId);
        return ResponseEntity.noContent().build();
    }
}
