package br.com.rts.eventmanager.seguranca.perfil.controllers;

import br.com.rts.eventmanager.seguranca.perfil.controllers.requests.PerfilRequest;
import br.com.rts.eventmanager.seguranca.perfil.controllers.responses.PerfilResponse;
import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.perfil.mappers.PerfilMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController("Perfis")
@RequestMapping("/api/v1/perfis")
@Tag(name = "Perfis", description = "API para gerenciamento de perfis de acesso")
public class PerfilRestController {

    private final PerfilService service;
    private final PerfilMapper mapper;

    @PostMapping
    @Operation(summary = "Criar um novo perfil de acesso para uma instituição", 
               description = "Cadastra um perfil associado a uma instituição com um conjunto de permissões padrão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou nome de perfil duplicado"),
            @ApiResponse(responseCode = "404", description = "Instituição ou permissão não encontradas")
    })
    public ResponseEntity<PerfilResponse> create(@RequestBody @Valid PerfilRequest request) {
        Perfil perfil = Perfil.builder()
                .instituicao(request.instituicao())
                .nome(request.nome())
                .build();

        try {
            List<Long> permissaoIds = request.permissaoIds() != null 
                    ? new ArrayList<>(request.permissaoIds()) 
                    : new ArrayList<>();
            Perfil perfilCriado = service.create(perfil, permissaoIds);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(perfilCriado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{perfilId}")
    @Operation(summary = "Obter detalhes de um perfil de acesso", 
               description = "Retorna informações detalhadas do perfil pelo seu ID, incluindo as permissões atribuídas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<PerfilResponse> getById(
            @Parameter(description = "ID do perfil", required = true)
            @PathVariable Long perfilId) {
        try {
            Perfil perfil = service.get(perfilId);
            return ResponseEntity.ok(mapper.entityToResponse(perfil));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/instituicoes/{instituicaoId}")
    @Operation(summary = "Listar perfis de acesso vinculados a uma instituição", 
               description = "Retorna todos os perfis cadastrados para a instituição informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfis listados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<List<PerfilResponse>> listByInstituicao(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long instituicaoId) {
        try {
            List<Perfil> perfis = service.listByInstituicao(instituicaoId);
            return ResponseEntity.ok(mapper.entityToResponse(perfis));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
