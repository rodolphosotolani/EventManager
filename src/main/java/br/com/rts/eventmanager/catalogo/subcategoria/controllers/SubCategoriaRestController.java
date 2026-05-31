package br.com.rts.eventmanager.catalogo.subcategoria.controllers;

import br.com.rts.eventmanager.catalogo.subcategoria.controllers.requests.SubCategoriaRequest;
import br.com.rts.eventmanager.catalogo.subcategoria.controllers.responses.SubCategoriaResponse;
import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
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
@RestController("SubCategorias")
@RequestMapping("/api/v1/subcategorias")
@Tag(name = "Sub-Categorias", description = "API para gerenciamento do catálogo de sub-categorias")
public class SubCategoriaRestController {

    private final SubCategoriaService service;
    private final SubCategoriaMapper mapper;

    @GetMapping()
    @Operation(summary = "Listar todas as sub-categorias por instituição",
            description = "Retorna uma página com a lista de sub-categorias vinculadas a uma instituição específica informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sub-categorias retornadas com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma sub-categoria cadastrada para esta instituição")
    })
    public ResponseEntity<Page<SubCategoriaResponse>> getAllSubCategorias(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            Pageable pageable) {

        Page<SubCategoria> subCategorias = service.findAllByInstituicao(instituicaoId, pageable);

        if (subCategorias == null || subCategorias.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity
                .ok(subCategorias.map(mapper::entityToResponse));
    }

    @GetMapping("/{subCategoriaId}")
    @Operation(summary = "Obter detalhes de uma sub-categoria específica",
            description = "Retorna os detalhes de uma única sub-categoria pelo seu ID e o ID da instituição associada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sub-categoria localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sub-categoria não encontrada para esta instituição")
    })
    public ResponseEntity<SubCategoriaResponse> getSubCategoriaById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da sub-categoria", required = true)
            @PathVariable Long subCategoriaId) {

        SubCategoria subCategoria = service.findByIdAndInstituicao(subCategoriaId, instituicaoId);

        if (Objects.isNull(subCategoria))
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .ok(mapper.entityToResponse(subCategoria));
    }

    @PostMapping()
    @Operation(summary = "Cadastrar uma nova sub-categoria",
            description = "Cadastra uma nova sub-categoria no sistema a partir dos dados fornecidos no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sub-categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    public ResponseEntity<SubCategoriaResponse> createSubCategoria(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados de criação da sub-categoria", required = true)
            @RequestBody SubCategoriaRequest request) {

        SubCategoria subCategoria = mapper.requestToEntity(request);

        SubCategoria subCategoriaCriada = service.create(subCategoria, instituicaoId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.entityToResponse(subCategoriaCriada));
    }

    @PutMapping("/{subCategoriaId}")
    @Operation(summary = "Atualizar uma sub-categoria existente",
            description = "Atualiza os dados de uma sub-categoria pelo ID no path, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sub-categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sub-categoria não encontrada para esta instituição"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<SubCategoriaResponse> updateSubCategoria(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da sub-categoria a ser atualizada", required = true)
            @PathVariable Long subCategoriaId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody SubCategoriaRequest request) {

        SubCategoria subCategoriaUpdate = mapper.requestToEntity(request);

        SubCategoria subCategoriaAtualizada = service.update(subCategoriaId, subCategoriaUpdate, instituicaoId);

        return ResponseEntity.ok(mapper.entityToResponse(subCategoriaAtualizada));
    }

    @DeleteMapping("/{subCategoriaId}")
    @Operation(summary = "Excluir uma sub-categoria existente",
            description = "Remove definitivamente uma sub-categoria pelo seu ID se ela pertencer à instituição informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sub-categoria deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sub-categoria não localizada ou não pertence à instituição")
    })
    public ResponseEntity<Void> deleteSubCategoria(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da sub-categoria a ser deletada", required = true)
            @PathVariable Long subCategoriaId) {

        service.delete(subCategoriaId, instituicaoId);

        return ResponseEntity.noContent().build();
    }
}
