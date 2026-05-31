package br.com.rts.eventmanager.catalogo.movimentacao.controllers;

import br.com.rts.eventmanager.catalogo.movimentacao.controllers.requests.MovimentacaoRequest;
import br.com.rts.eventmanager.catalogo.movimentacao.controllers.responses.MovimentacaoResponse;
import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.movimentacao.mappers.MovimentacaoMapper;
import br.com.rts.eventmanager.catalogo.movimentacao.services.MovimentacaoService;
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

import java.util.Objects;

@RequiredArgsConstructor
@RestController("Movimentacoes")
@RequestMapping("/api/v1/movimentacoes")
@Tag(name = "Movimentacoes", description = "API para gerenciamento de movimentações do catálogo")
public class MovimentacaoRestController {

    private final MovimentacaoService service;
    private final MovimentacaoMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar movimentações por instituição e evento",
            description = "Retorna uma página com a lista de movimentações vinculadas a uma instituição e a um evento específicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentações retornadas com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma movimentação cadastrada para esta instituição neste evento")
    })
    public ResponseEntity<Page<MovimentacaoResponse>> getAllMovimentacoes(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<Movimentacao> movimentacoes = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        if (movimentacoes == null || movimentacoes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(movimentacoes.map(mapper::entityToResponse));
    }

    @GetMapping("/{movimentacaoId}")
    @Operation(summary = "Obter detalhes de uma movimentação específica (Busca direta por ID)",
            description = "Retorna os detalhes de um único registro de movimentação pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentação localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Movimentação não encontrada para esta instituição")
    })
    public ResponseEntity<MovimentacaoResponse> getMovimentacaoById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da movimentação", required = true)
            @PathVariable Long movimentacaoId) {

        Movimentacao movimentacao = service.findByIdAndInstituicao(movimentacaoId, instituicaoId);

        if (Objects.isNull(movimentacao)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.entityToResponse(movimentacao));
    }

    @PostMapping("/eventos/{eventoId}")
    @Operation(summary = "Cadastrar uma nova movimentação (Evento informado no payload)",
            description = "Cadastra um novo registro de movimentação. O cabeçalho deve conter o ID da instituição e os dados de movimentação (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimentação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<MovimentacaoResponse> createMovimentacao(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "Dados da movimentação com evento no payload", required = true)
            @RequestBody MovimentacaoRequest request) {

        Movimentacao movimentacao = mapper.requestToEntity(request, instituicaoId, eventoId);

        Movimentacao movimentacaoCriada = service.create(movimentacao);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.entityToResponse(movimentacaoCriada));
    }

    @PutMapping("/eventos/{eventoId}/{movimentacaoId}")
    @Operation(summary = "Atualizar uma movimentação existente",
            description = "Atualiza os dados de uma movimentação pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentação atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Movimentação não encontrada para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<MovimentacaoResponse> updateMovimentacao(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID da movimentação a ser atualizada", required = true)
            @PathVariable Long movimentacaoId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody MovimentacaoRequest request) {

        Movimentacao movimentacaoExistente = service.findByIdAndInstituicaoAndEvento(movimentacaoId, instituicaoId, eventoId);
        if (Objects.isNull(movimentacaoExistente)) {
            return ResponseEntity.notFound().build();
        }

        Movimentacao movimentacaoUpdate = mapper.updateEntity(movimentacaoExistente, request);

        Movimentacao movimentacaoAtualizada = service.update(movimentacaoId, movimentacaoUpdate);

        return ResponseEntity.ok(mapper.entityToResponse(movimentacaoAtualizada));
    }

    @DeleteMapping("/eventos/{eventoId}/{movimentacaoId}")
    @Operation(summary = "Excluir uma movimentação existente",
            description = "Remove definitivamente uma movimentação pelo seu ID se ela pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimentação deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Movimentação não localizada ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> deleteMovimentacao(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID da movimentação a ser deletada", required = true)
            @PathVariable Long movimentacaoId) {

        Movimentacao movimentacao = service.findByIdAndInstituicaoAndEvento(movimentacaoId, instituicaoId, eventoId);
        if (Objects.isNull(movimentacao)) {
            return ResponseEntity.notFound().build();
        }

        service.delete(movimentacaoId);
        return ResponseEntity.noContent().build();
    }
}
