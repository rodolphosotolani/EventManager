package br.com.rts.eventmanager.financeiro.conta.controllers;

import br.com.rts.eventmanager.financeiro.conta.controllers.requests.ContaRequest;
import br.com.rts.eventmanager.financeiro.conta.controllers.responses.ContaResponse;
import br.com.rts.eventmanager.financeiro.conta.entities.Conta;
import br.com.rts.eventmanager.financeiro.conta.mappers.ContaMapper;
import br.com.rts.eventmanager.financeiro.conta.services.ContaService;
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
@RestController("Contas")
@RequestMapping("/api/v1/contas")
@Tag(name = "Contas", description = "API para gerenciamento de contas a receber")
public class ContaRestController {

    private final ContaService service;
    private final ContaMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar todas as contas por instituição e evento", 
               description = "Retorna uma página contendo todas as contas vinculadas à instituição no cabeçalho e ao evento no path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas retornadas com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma conta cadastrada para esta instituição neste evento")
    })
    public ResponseEntity<Page<ContaResponse>> getAllContas(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<Conta> contas = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        if (contas == null || contas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(contas.map(mapper::entityToResponse));
    }

    @GetMapping("/{contaId}")
    @Operation(summary = "Obter detalhes de uma conta específica", 
               description = "Retorna os detalhes de um único registro de conta pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada para esta instituição")
    })
    public ResponseEntity<ContaResponse> getContaById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do registro de conta", required = true)
            @PathVariable Long contaId) {

        Optional<Conta> contaOptional = service.findByIdAndInstituicao(contaId, instituicaoId);

        return contaOptional
                .map(conta -> ResponseEntity.ok(mapper.entityToResponse(conta)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Operation(summary = "Cadastrar uma nova conta (Evento informado no payload)", 
               description = "Cadastra uma nova conta. O cabeçalho deve conter o ID da instituição e os dados de conta (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<ContaResponse> create(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados para criação da conta com evento no payload", required = true)
            @RequestBody ContaRequest request) {


        Conta conta = mapper.requestToEntity(request);

        try {
            Conta contaCriada = service.create(conta, instituicaoId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(contaCriada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{contaId}")
    @Operation(summary = "Atualizar uma conta existente", 
               description = "Atualiza os dados de uma conta pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ContaResponse> update(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da conta a ser atualizada", required = true)
            @PathVariable Long contaId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody ContaRequest request) {

        Conta contaUpdate = mapper.requestToEntity(request);

        Conta contaAtualizada = service.update(contaId, contaUpdate, instituicaoId);

        return ResponseEntity.ok(mapper.entityToResponse(contaAtualizada));
    }

    @DeleteMapping("/eventos/{eventoId}/{contaId}")
    @Operation(summary = "Excluir uma conta existente", 
               description = "Remove definitivamente uma conta pelo seu ID se ela pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não localizada ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID da conta a ser deletada", required = true)
            @PathVariable Long contaId) {

        Optional<Conta> optionalConta = service.findByIdAndInstituicaoAndEvento(contaId, instituicaoId, eventoId);
        if (optionalConta.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(contaId);
        return ResponseEntity.noContent().build();
    }
}
