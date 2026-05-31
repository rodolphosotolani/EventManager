package br.com.rts.eventmanager.seguranca.usuario.controllers;

import br.com.rts.eventmanager.seguranca.perfil.controllers.responses.PerfilResponse;
import br.com.rts.eventmanager.seguranca.usuario.controllers.requests.UsuarioRequest;
import br.com.rts.eventmanager.seguranca.usuario.controllers.responses.UsuarioResponse;
import br.com.rts.eventmanager.seguranca.perfil.mappers.PerfilMapper;
import br.com.rts.eventmanager.seguranca.usuario.mappers.UsuarioMapper;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
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

import java.util.List;

@RequiredArgsConstructor
@RestController("Usuarios")
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "API para gerenciamento de usuários e controle de acessos")
public class UsuarioRestController {

    private final UsuarioService service;
    private final UsuarioMapper mapper;
    private final PerfilMapper perfilMapper;

    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário independente de instituição", 
               description = "Cadastra um novo usuário no sistema sem qualquer vínculo prévio a uma instituição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou e-mail já cadastrado")
    })
    public ResponseEntity<UsuarioResponse> create(@RequestBody @Valid UsuarioRequest request) {
        try {
            Usuario usuario = mapper.requestToEntity(request);
            Usuario usuarioCriado = service.create(usuario);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(usuarioCriado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Obter detalhes de um usuário", 
               description = "Retorna informações detalhadas do usuário pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponse> getById(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long usuarioId) {
        try {
            Usuario usuario = service.get(usuarioId);
            return ResponseEntity.ok(mapper.entityToResponse(usuario));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários do sistema", 
               description = "Retorna uma lista contendo todos os usuários cadastrados globalmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso")
    })
    public ResponseEntity<List<UsuarioResponse>> list() {
        return ResponseEntity.ok(mapper.entityToResponse(service.list()));
    }

    @PostMapping("/{usuarioId}/instituicoes/{instituicaoId}")
    @Operation(summary = "Vincular usuário a uma instituição", 
               description = "Associa um usuário existente a uma instituição específica do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário vinculado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou instituição não encontrados"),
            @ApiResponse(responseCode = "400", description = "Operação inválida")
    })
    public ResponseEntity<Void> linkToInstituicao(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long instituicaoId) {
        try {
            service.linkToInstituicao(usuarioId, instituicaoId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{usuarioId}/eventos/{eventoId}/perfis/{perfilId}")
    @Operation(summary = "Atribuir perfil de acesso ao usuário em um evento específico", 
               description = "Vincula um perfil de acesso ao usuário para um evento determinado, garantindo que o perfil e o evento pertençam à mesma instituição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atribuído ao evento com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário, perfil ou evento não encontrados"),
            @ApiResponse(responseCode = "400", description = "Perfil ou evento inválido ou usuário não vinculado à instituição")
    })
    public ResponseEntity<Void> assignPerfil(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do perfil de acesso", required = true)
            @PathVariable Long perfilId) {
        try {
            service.assignPerfilToEvent(usuarioId, perfilId, eventoId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{usuarioId}/eventos/{eventoId}/perfis")
    @Operation(summary = "Listar perfis atribuídos a um usuário em um evento específico", 
               description = "Retorna todos os perfis de acesso vinculados ao usuário para as ações e escopo do evento informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfis retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou evento não encontrados")
    })
    public ResponseEntity<List<PerfilResponse>> listPerfisByEvent(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId) {
        try {
            return ResponseEntity.ok(perfilMapper.entityToResponse(service.listPerfisByEvent(usuarioId, eventoId)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
