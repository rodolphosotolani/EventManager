package br.com.rts.eventmanager.catalogo.categoria.controllers;

import br.com.rts.eventmanager.catalogo.categoria.controllers.requests.CategoriaRequest;
import br.com.rts.eventmanager.catalogo.categoria.controllers.responses.CategoriaResponse;
import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
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

@RequiredArgsConstructor
@RestController("Categorias")
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorias", description = "API para gerenciamento do catálogo de categorias")
public class CategoriaRestController {

    private final CategoriaService service;
    private final CategoriaMapper mapper;

    @GetMapping()
    @Operation(summary = "Listar todas as categorias por instituição",
            description = "Retorna uma página com a lista de categorias vinculadas a uma instituição específica informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma categoria cadastrada para esta instituição")
    })
    public ResponseEntity<Page<CategoriaResponse>> getAllCategorias(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            Pageable pageable) {

        Page<Categoria> categorias = service.findAllByInstituicao(instituicaoId, pageable);

        if (categorias == null || categorias.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity
                .ok(categorias.map(mapper::entityToResponse));
    }

    @GetMapping("/{categoriaId}")
    @Operation(summary = "Obter detalhes de uma categoria específica",
            description = "Retorna os detalhes de uma única categoria pelo seu ID e o ID da instituição associada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria localizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada para esta instituição")
    })
    public ResponseEntity<CategoriaResponse> getCategoriaById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da categoria", required = true)
            @PathVariable Long categoriaId) {

        Categoria categoria = service.findByIdAndInstituicao(categoriaId, instituicaoId);

        return ResponseEntity
                .ok(mapper.entityToResponse(categoria));
    }

    @PostMapping()
    @Operation(summary = "Cadastrar uma nova categoria",
            description = "Cadastra uma nova categoria no sistema a partir dos dados fornecidos no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    public ResponseEntity<CategoriaResponse> createCategoria(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados de criação da categoria", required = true)
            @RequestBody CategoriaRequest request) {

        Categoria categoria = mapper.requestToEntity(request);

        Categoria categoriaCriada = service.create(instituicaoId, categoria);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.entityToResponse(categoriaCriada));
    }

    @PutMapping("/{categoriaId}")
    @Operation(summary = "Atualizar uma categoria existente",
            description = "Atualiza os dados de uma categoria pelo ID no path, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada para esta instituição"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<CategoriaResponse> updateCategoria(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da categoria a ser atualizada", required = true)
            @PathVariable Long categoriaId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody CategoriaRequest request) {

        Categoria categoriaUpdate = mapper.requestToEntity(request);

        Categoria categoriaAtualizada = service.update(instituicaoId, categoriaId, categoriaUpdate);

        return ResponseEntity.ok(mapper.entityToResponse(categoriaAtualizada));
    }

    @DeleteMapping("/{categoriaId}")
    @Operation(summary = "Excluir uma categoria existente",
            description = "Remove definitivamente uma categoria pelo seu ID se ela pertencer à instituição informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não localizada ou não pertence à instituição")
    })
    public ResponseEntity<Void> deleteCategoria(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID da categoria a ser deletada", required = true)
            @PathVariable Long categoriaId) {

        service.delete(instituicaoId, categoriaId);
        return ResponseEntity.noContent().build();
    }
}
