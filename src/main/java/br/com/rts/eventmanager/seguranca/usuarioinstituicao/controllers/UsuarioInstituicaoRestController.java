package br.com.rts.eventmanager.seguranca.usuarioinstituicao.controllers;

import br.com.rts.eventmanager.seguranca.usuarioinstituicao.services.UsuarioInstituicaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("Usuarios por Instituicao")
@RequestMapping("/api/v1/usuarios-instituicao")
@Tag(name = "Usuarios por Instituicao",
        description = "API para gerenciamento de usuários por Instituições e controle de acessos")
public class UsuarioInstituicaoRestController {

    private final UsuarioInstituicaoService service;


    @PostMapping("/{usuarioId}/instituicoes/{instituicaoId}/vincular")
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

    @PostMapping("/{usuarioId}/instituicoes/{instituicaoId}/desvincular")
    @Operation(summary = "Desvincular usuário a uma instituição",
            description = "Remove a associação de um usuário a uma instituição específica do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário vinculado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou instituição não encontrados"),
            @ApiResponse(responseCode = "400", description = "Operação inválida")
    })
    public ResponseEntity<Void> unlinkToInstituicao(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long instituicaoId) {
        try {

            service.unlinkToInstituicao(usuarioId, instituicaoId);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
