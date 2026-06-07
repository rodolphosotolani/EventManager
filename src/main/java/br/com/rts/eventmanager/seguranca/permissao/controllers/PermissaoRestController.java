package br.com.rts.eventmanager.seguranca.permissao.controllers;

import br.com.rts.eventmanager.seguranca.permissao.controllers.responses.PermissaoResponse;
import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.permissao.mappers.PermissaoMapper;
import br.com.rts.eventmanager.seguranca.permissao.services.PermissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController("Permissoes")
@RequestMapping("/api/v1/permissoes")
@Tag(name = "Permissões", description = "API para consulta de permissões de acesso")
public class PermissaoRestController {

    private final PermissaoService service;
    private final PermissaoMapper mapper;

    @GetMapping
    @Operation(summary = "Listar todas as permissões de acesso",
            description = "Retorna uma lista com todas as permissões cadastradas no sistema. Pode ser filtrada por tela.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissões listadas com sucesso")
    })
    public ResponseEntity<Page<PermissaoResponse>> listAll(
            @Parameter(description = "Filtrar por tela") @RequestParam(required = false) String tela,
            Pageable pageable) {

        Page<Permissao> permissoes = service.findAllByTela(tela, pageable);

        return ResponseEntity.ok(permissoes.map(mapper::entityToResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de uma permissão",
            description = "Retorna os detalhes de uma permissão pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Permissão não encontrada")
    })
    public ResponseEntity<PermissaoResponse> getById(
            @Parameter(description = "ID da permissão", required = true)
            @PathVariable Long id) {
        try {
            Permissao permissao = service.findById(id);
            return ResponseEntity.ok(mapper.entityToResponse(permissao));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

