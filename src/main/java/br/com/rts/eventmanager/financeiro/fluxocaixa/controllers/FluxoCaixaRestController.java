package br.com.rts.eventmanager.financeiro.fluxocaixa.controllers;

import br.com.rts.eventmanager.financeiro.fluxocaixa.controllers.requests.FluxoCaixaRequest;
import br.com.rts.eventmanager.financeiro.fluxocaixa.controllers.responses.FluxoCaixaResponse;
import br.com.rts.eventmanager.financeiro.fluxocaixa.entities.FluxoCaixa;
import br.com.rts.eventmanager.financeiro.fluxocaixa.mappers.FluxoCaixaMapper;
import br.com.rts.eventmanager.financeiro.fluxocaixa.services.FluxoCaixaService;
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
@RestController("FluxosCaixa")
@RequestMapping("/api/v1/fluxos-caixa")
@Tag(name = "FluxosCaixa", description = "API para gerenciamento de fluxo de caixa")
public class FluxoCaixaRestController {

    private final FluxoCaixaService service;
    private final FluxoCaixaMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar todos os fluxos de caixa por instituição e evento",
            description = "Retorna uma página contendo todos os fluxos de caixa vinculados à instituição no cabeçalho e ao evento no path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fluxos de caixa retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum registro de fluxo de caixa cadastrado para esta instituição neste evento")
    })
    public ResponseEntity<Page<FluxoCaixaResponse>> getAllFluxos(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<FluxoCaixa> fluxos = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        if (fluxos == null || fluxos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(fluxos.map(mapper::entityToResponse));
    }

    @GetMapping("/{fluxoCaixaId}")
    @Operation(summary = "Obter detalhes de um fluxo de caixa específico",
            description = "Retorna os detalhes de um único registro de fluxo de caixa pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fluxo de caixa localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado para esta instituição")
    })
    public ResponseEntity<FluxoCaixaResponse> getFluxoById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do fluxo de caixa", required = true)
            @PathVariable Long fluxoCaixaId) {

        Optional<FluxoCaixa> fluxoOptional = service.findByIdAndInstituicao(fluxoCaixaId, instituicaoId);

        return fluxoOptional
                .map(fluxo -> ResponseEntity.ok(mapper.entityToResponse(fluxo)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Operation(summary = "Cadastrar um novo fluxo de caixa (Evento informado no payload)",
            description = "Cadastra um novo fluxo de caixa. O cabeçalho deve conter o ID da instituição e os dados de fluxo de caixa (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fluxo de caixa criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<FluxoCaixaResponse> create(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados para criação do fluxo de caixa com evento no payload", required = true)
            @RequestBody FluxoCaixaRequest request) {

        if (request.evento() == null) {
            return ResponseEntity.badRequest().build();
        }

        FluxoCaixa fluxoCaixa = mapper.requestToEntity(request);
        fluxoCaixa.setInstituicao(instituicaoId);

        try {
            FluxoCaixa fluxoCriado = service.create(fluxoCaixa);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(fluxoCriado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{fluxoCaixaId}")
    @Operation(summary = "Atualizar um fluxo de caixa existente",
            description = "Atualiza os dados de um fluxo de caixa pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fluxo de caixa atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<FluxoCaixaResponse> update(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do fluxo de caixa a ser atualizado", required = true)
            @PathVariable Long fluxoCaixaId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody FluxoCaixaRequest request) {

        FluxoCaixa fluxoUpdate = mapper.requestToEntity(request);

        try {

            FluxoCaixa fluxoAtualizado = service.update(fluxoCaixaId, fluxoUpdate, instituicaoId);

            return ResponseEntity.ok(mapper.entityToResponse(fluxoAtualizado));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eventos/{eventoId}/{fluxoCaixaId}")
    @Operation(summary = "Excluir um fluxo de caixa existente",
            description = "Remove definitivamente um fluxo de caixa pelo seu ID se ele pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fluxo de caixa deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não localizado ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do fluxo de caixa a ser deletado", required = true)
            @PathVariable Long fluxoCaixaId) {

        Optional<FluxoCaixa> optionalFluxo = service.findByIdAndInstituicaoAndEvento(fluxoCaixaId, instituicaoId, eventoId);
        if (optionalFluxo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(fluxoCaixaId);
        return ResponseEntity.noContent().build();
    }
}
