package br.com.rts.eventmanager.catalogo.estoque.controllers;

import br.com.rts.eventmanager.catalogo.estoque.controllers.requests.EstoqueRequest;
import br.com.rts.eventmanager.catalogo.estoque.controllers.responses.EstoqueResponse;
import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.estoque.mappers.EstoqueMapper;
import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
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
@RestController("Estoques")
@RequestMapping("/api/v1/estoques")
@Tag(name = "Estoques", description = "API para gerenciamento do catálogo de estoques")
public class EstoqueRestController {

    private final EstoqueService service;
    private final EstoqueMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar estoques por instituição e evento",
            description = "Retorna uma página com a lista de estoques vinculados a uma instituição e a um evento específicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoques retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum estoque cadastrado para esta instituição neste evento")
    })
    public ResponseEntity<Page<EstoqueResponse>> getAllEstoques(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<Estoque> estoques = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        return ResponseEntity
                .ok(estoques.map(mapper::entityToResponse));
    }

    @GetMapping("/eventos/{eventoId}/{estoqueId}")
    @Operation(summary = "Obter detalhes de um estoque específico (Busca direta por ID)",
            description = "Retorna os detalhes de um único registro de estoque pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado para esta instituição")
    })
    public ResponseEntity<EstoqueResponse> getEstoqueById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do registro de estoque", required = true)
            @PathVariable Long estoqueId) {

        Estoque estoque = service.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, eventoId);

        if (Objects.isNull(estoque))
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .ok(mapper.entityToResponse(estoque));
    }

    @PostMapping()
    @Operation(summary = "Cadastrar um novo estoque (Evento informado no payload)",
            description = "Cadastra um novo registro de estoque. O cabeçalho deve conter o ID da instituição e os dados de estoque (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estoque criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<EstoqueResponse> createEstoque(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados do estoque com evento no payload", required = true)
            @RequestBody EstoqueRequest request) {

        Estoque estoque = mapper.requestToEntity(request);

        Estoque estoqueCriado = service.create(estoque, instituicaoId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.entityToResponse(estoqueCriado));
    }

    @PatchMapping("/{estoqueId}/adicionar")
    @Operation(summary = "Adiciona uma quantidade a um estoque existente",
            description = "Atualiza os dados de um estoque pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<EstoqueResponse> adicionaAoEstoque(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do registro de estoque a ser atualizado", required = true)
            @PathVariable Long estoqueId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody EstoqueRequest request) {

        Estoque estoqueUpdate = mapper.requestToEntity(request);

        Estoque estoqueAtualizado = service.adicionaAoEstoque(estoqueId, estoqueUpdate, instituicaoId);

        return ResponseEntity.ok(mapper.entityToResponse(estoqueAtualizado));
    }

    @PatchMapping("/{estoqueId}/subtrair")
    @Operation(summary = "Subtrai uma quantidade a um estoque existente",
            description = "Atualiza os dados de um estoque pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<EstoqueResponse> subtrairDoEstoque(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do registro de estoque a ser atualizado", required = true)
            @PathVariable Long estoqueId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody EstoqueRequest request) {

        Estoque estoqueUpdate = mapper.requestToEntity(request);

        Estoque estoqueAtualizado = service.subtrairDoEstoque(estoqueId, estoqueUpdate, instituicaoId);

        return ResponseEntity.ok(mapper.entityToResponse(estoqueAtualizado));
    }

    @DeleteMapping("/eventos/{eventoId}/{estoqueId}")
    @Operation(summary = "Excluir um estoque existente",
            description = "Remove definitivamente um estoque pelo seu ID se ele pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estoque deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estoque não localizado ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> deleteEstoque(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do registro de estoque a ser deletado", required = true)
            @PathVariable Long estoqueId) {

        service.delete(estoqueId, instituicaoId, eventoId);

        return ResponseEntity.noContent().build();
    }
}
