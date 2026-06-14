package br.com.rts.eventmanager.seguranca.perfilusuario.controllers;

import br.com.rts.eventmanager.seguranca.perfilusuario.controllers.responses.PerfilUsuarioResponse;
import br.com.rts.eventmanager.seguranca.perfilusuario.mappers.PerfilUsuarioMapper;
import br.com.rts.eventmanager.seguranca.perfilusuario.services.PerfilUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController("Perfil do Usuario")
@RequestMapping("/api/v1/perfils-usuarios")
@Tag(name = "Perfis do Usuarios", description = "API para gerenciamento dos perfis de usuários e controle de acessos")
public class PerfilUsuarioRestController {

    private final PerfilUsuarioService service;
    private final PerfilUsuarioMapper mapper;

    @PostMapping("/{usuarioId}/instituicoes/{instituicaoId}/perfis/{perfilId}")
    @Operation(summary = "Atribuir perfil de acesso ao usuário para uma instituição específica",
            description = "Associa um perfil de acesso ao usuário no contexto da instituição informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atribuído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário, perfil ou instituição não encontrados"),
            @ApiResponse(responseCode = "400", description = "Perfil ou instituição inválido")
    })
    public ResponseEntity<Void> assignPerfil(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long instituicaoId,
            @Parameter(description = "ID do perfil de acesso", required = true)
            @PathVariable Long perfilId) {
        try {
            service.assignPerfilToInstituicao(usuarioId, instituicaoId, perfilId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{usuarioId}/instituicoes/{instituicaoId}/perfis")
    @Operation(summary = "Listar perfis atribuídos a um usuário em uma instituição específica",
            description = "Retorna todos os perfis de acesso vinculados ao usuário para as ações e escopo da instituição informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfis retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou instituição não encontrados")
    })
    public ResponseEntity<List<PerfilUsuarioResponse>> listPerfisByInstituicao(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long instituicaoId) {
        try {
            return ResponseEntity.ok(
                    mapper.entityToResponse(
                            service.findAllByUsuarioIdAndInstituicao(usuarioId, instituicaoId)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
