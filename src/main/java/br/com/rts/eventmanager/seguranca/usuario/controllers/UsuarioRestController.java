package br.com.rts.eventmanager.seguranca.usuario.controllers;

import br.com.rts.eventmanager.seguranca.usuario.controllers.requests.UsuarioRequest;
import br.com.rts.eventmanager.seguranca.usuario.controllers.responses.UsuarioResponse;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.mappers.UsuarioMapper;
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
            Usuario usuario = service.getUsuarioById(usuarioId);
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
}
