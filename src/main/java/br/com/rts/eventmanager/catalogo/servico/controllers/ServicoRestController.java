package br.com.rts.eventmanager.catalogo.servico.controllers;

import br.com.rts.eventmanager.catalogo.servico.controllers.requests.ServicoRequest;
import br.com.rts.eventmanager.catalogo.servico.controllers.responses.ServicoResponse;
import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import br.com.rts.eventmanager.catalogo.servico.mappers.ServicoMapper;
import br.com.rts.eventmanager.catalogo.servico.services.ServicoService;
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
@RestController("Servicos")
@RequestMapping("/api/v1/servicos")
@Tag(name = "Serviços", description = "API para gerenciamento do catálogo de serviços")
public class ServicoRestController {

    private final ServicoService service;
    private final ServicoMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar serviços por instituição e evento",
            description = "Retorna uma página com a lista de serviços vinculados a uma instituição e a um evento específicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviços retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum serviço cadastrado para esta instituição neste evento")
    })
    public ResponseEntity<Page<ServicoResponse>> getAllServicos(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<Servico> servicos = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        if (servicos == null || servicos.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity
                .ok(servicos.map(mapper::entityToResponse));
    }

    @GetMapping("/eventos/{eventoId}/{servicoId}")
    @Operation(summary = "Obter detalhes de um serviço específico (Busca direta por ID)",
            description = "Retorna os detalhes de um único serviço pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado para esta instituição")
    })
    public ResponseEntity<ServicoResponse> getServicoById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do serviço", required = true)
            @PathVariable Long servicoId) {

        Servico servico = service.findByInstituicaoAndEvento(servicoId, instituicaoId, eventoId);

        if (Objects.isNull(servico))
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .ok(mapper.entityToResponse(servico));
    }

    @PostMapping("/eventos/{eventoId}")
    @Operation(summary = "Cadastrar um novo serviço (Evento informado no payload)",
            description = "Cadastra um novo serviço. O cabeçalho deve conter o ID da instituição e os dados do serviço (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<ServicoResponse> createServico(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "Dados do serviço com evento no payload", required = true)
            @RequestBody ServicoRequest request) {

        Servico servico = mapper.requestToEntity(request);

        Servico servicoCriado = service.create(servico, instituicaoId, eventoId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.entityToResponse(servicoCriado));
    }

    @PutMapping("/eventos/{eventoId}/{servicoId}")
    @Operation(summary = "Atualizar um serviço existente",
            description = "Atualiza os dados de um serviço pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ServicoResponse> updateServico(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do serviço a ser atualizado", required = true)
            @PathVariable Long servicoId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody ServicoRequest request) {

        Servico servicoUpdate = mapper.requestToEntity(request);

        Servico servicoAtualizado = service.update(servicoId, servicoUpdate, instituicaoId, eventoId);

        return ResponseEntity.ok(mapper.entityToResponse(servicoAtualizado));
    }

    @DeleteMapping("/eventos/{eventoId}/{servicoId}")
    @Operation(summary = "Excluir um serviço existente",
            description = "Remove definitivamente um serviço pelo seu ID se ele pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não localizado ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> deleteServico(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do serviço a ser deletado", required = true)
            @PathVariable Long servicoId) {

        service.delete(servicoId, instituicaoId, eventoId);

        return ResponseEntity.noContent().build();
    }
}
