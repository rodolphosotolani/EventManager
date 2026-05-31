package br.com.rts.eventmanager.gestao.instituicao.controllers;

import br.com.rts.eventmanager.gestao.instituicao.controllers.requests.InstituicaoRequest;
import br.com.rts.eventmanager.gestao.instituicao.controllers.responses.InstituicaoResponse;
import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import br.com.rts.eventmanager.gestao.instituicao.mappers.InstituicaoMapper;
import br.com.rts.eventmanager.gestao.instituicao.services.InstituicaoService;
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
@RestController("Instituicoes")
@RequestMapping("/api/v1/instituicoes")
@Tag(name = "Instituições", description = "API para gerenciamento de instituições (tenants) do sistema")
public class InstituicaoRestController {

    private final InstituicaoService service;
    private final InstituicaoMapper mapper;

    @GetMapping()
    @Operation(summary = "Listar todas as instituições", 
               description = "Retorna uma página contendo todas as instituições cadastradas no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituições retornadas com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma instituição cadastrada")
    })
    public ResponseEntity<Page<InstituicaoResponse>> getAllInstituicoes(
            Pageable pageable) {

        Page<Instituicao> instituicoes = service.findAll(pageable);

        if (instituicoes == null || instituicoes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(instituicoes.map(mapper::entityToResponse));
    }

    @GetMapping("/{instituicaoId}")
    @Operation(summary = "Obter detalhes de uma instituição específica", 
               description = "Retorna os detalhes de uma única instituição cadastrada pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<InstituicaoResponse> getInstituicaoById(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long instituicaoId) {

        Optional<Instituicao> instituicaoOptional = service.findById(instituicaoId);

        return instituicaoOptional
                .map(instituicao -> ResponseEntity.ok(mapper.entityToResponse(instituicao)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Operation(summary = "Cadastrar uma nova instituição", 
               description = "Cadastra uma nova instituição no sistema a partir dos dados fornecidos no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Instituição criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    public ResponseEntity<InstituicaoResponse> create(
            @Parameter(description = "Dados para criação da instituição", required = true)
            @RequestBody InstituicaoRequest request) {

        Instituicao instituicao = mapper.requestToEntity(request);

        try {
            Instituicao instituicaoCriada = service.create(instituicao);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(instituicaoCriada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{instituicaoId}")
    @Operation(summary = "Atualizar uma instituição existente", 
               description = "Atualiza os dados de uma instituição a partir do seu ID e dos novos dados enviados no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<InstituicaoResponse> update(
            @Parameter(description = "ID da instituição a ser atualizada", required = true)
            @PathVariable Long instituicaoId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody InstituicaoRequest request) {

        Optional<Instituicao> optionalInstituicao = service.findById(instituicaoId);
        if (optionalInstituicao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instituicao instituicaoUpdate = mapper.requestToEntity(request);
        service.update(instituicaoId, instituicaoUpdate);

        Optional<Instituicao> instituicaoAtualizada = service.findById(instituicaoId);
        return instituicaoAtualizada
                .map(instituicao -> ResponseEntity.ok(mapper.entityToResponse(instituicao)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{instituicaoId}")
    @Operation(summary = "Excluir uma instituição existente", 
               description = "Remove definitivamente uma instituição pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Instituição deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição a ser deletada", required = true)
            @PathVariable Long instituicaoId) {

        Optional<Instituicao> optionalInstituicao = service.findById(instituicaoId);
        if (optionalInstituicao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(instituicaoId);
        return ResponseEntity.noContent().build();
    }
}
