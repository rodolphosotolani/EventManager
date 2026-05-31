package br.com.rts.eventmanager.gestao.evento.controllers;

import br.com.rts.eventmanager.gestao.evento.controllers.requests.EventoRequest;
import br.com.rts.eventmanager.gestao.evento.controllers.responses.EventoResponse;
import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import br.com.rts.eventmanager.gestao.evento.mappers.EventoMapper;
import br.com.rts.eventmanager.gestao.evento.services.EventoService;
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
@RestController("Eventos")
@RequestMapping("/api/v1/eventos")
@Tag(name = "Eventos", description = "API para gerenciamento de eventos do sistema")
public class EventoRestController {

    private final EventoService service;
    private final EventoMapper mapper;

    @GetMapping()
    @Operation(summary = "Listar todos os eventos por instituição", 
               description = "Retorna uma página contendo os eventos cadastrados para a instituição informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum evento cadastrado para esta instituição")
    })
    public ResponseEntity<Page<EventoResponse>> getAllEventos(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            Pageable pageable) {

        Page<Evento> eventos = service.findAllByInstituicao(instituicaoId, pageable);

        if (eventos == null || eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eventos.map(mapper::entityToResponse));
    }

    @GetMapping("/{eventoId}")
    @Operation(summary = "Obter detalhes de um evento específico", 
               description = "Retorna os detalhes de um único evento pelo seu ID se ele pertencer à instituição informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado para esta instituição")
    })
    public ResponseEntity<EventoResponse> getEventoById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId) {

        Optional<Evento> eventoOptional = service.findByIdAndInstituicao(eventoId, instituicaoId);

        return eventoOptional
                .map(evento -> ResponseEntity.ok(mapper.entityToResponse(evento)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Operation(summary = "Cadastrar um novo evento", 
               description = "Cadastra um novo evento no sistema sob a instituição informada no cabeçalho a partir dos dados fornecidos no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    public ResponseEntity<EventoResponse> create(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados de criação do evento", required = true)
            @RequestBody EventoRequest request) {

        Evento evento = mapper.requestToEntity(request);

        try {
            Evento eventoCriado = service.create(instituicaoId, evento);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(eventoCriado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{eventoId}")
    @Operation(summary = "Atualizar um evento existente", 
               description = "Atualiza os dados de um evento a partir do seu ID e dos novos dados enviados no corpo da requisição, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado para esta instituição"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<EventoResponse> update(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento a ser atualizado", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody EventoRequest request) {

        Optional<Evento> optionalEvento = service.findByIdAndInstituicao(eventoId, instituicaoId);
        if (optionalEvento.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Evento eventoUpdate = mapper.requestToEntity(request);
        service.update(eventoId, instituicaoId, eventoUpdate);

        Optional<Evento> eventoAtualizado = service.findByIdAndInstituicao(eventoId, instituicaoId);
        return eventoAtualizado
                .map(evento -> ResponseEntity.ok(mapper.entityToResponse(evento)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{eventoId}")
    @Operation(summary = "Excluir um evento existente", 
               description = "Remove definitivamente um evento pelo seu ID se ele pertencer à instituição informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado para esta instituição")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento a ser deletado", required = true)
            @PathVariable Long eventoId) {

        Optional<Evento> optionalEvento = service.findByIdAndInstituicao(eventoId, instituicaoId);
        if (optionalEvento.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(eventoId, instituicaoId);
        return ResponseEntity.noContent().build();
    }
}
