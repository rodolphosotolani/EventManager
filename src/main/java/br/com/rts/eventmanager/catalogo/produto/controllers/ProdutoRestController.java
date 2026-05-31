package br.com.rts.eventmanager.catalogo.produto.controllers;

import br.com.rts.eventmanager.catalogo.produto.controllers.requests.ProdutoRequest;
import br.com.rts.eventmanager.catalogo.produto.controllers.responses.ProdutoResponse;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
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
@RestController("Produtos")
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "API para gerenciamento do catálogo de produtos")
public class ProdutoRestController {

    private final ProdutoService service;
    private final ProdutoMapper mapper;

    @GetMapping("/eventos/{eventoId}")
    @Operation(summary = "Listar produtos por instituição e evento",
            description = "Retorna uma página com a lista de produtos vinculados a uma instituição e a um evento específicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum produto cadastrado para esta instituição neste evento")
    })
    public ResponseEntity<Page<ProdutoResponse>> getAllProdutos(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            Pageable pageable) {

        Page<Produto> produtos = service.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);

        if (produtos == null || produtos.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity
                .ok(produtos.map(mapper::entityToResponse));
    }

    @GetMapping("/eventos/{eventoId}/{produtoId}")
    @Operation(summary = "Obter detalhes de um produto específico (Busca direta por ID)",
            description = "Retorna os detalhes de um único produto pelo seu ID, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para esta instituição")
    })
    public ResponseEntity<ProdutoResponse> getProdutoById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do produto", required = true)
            @PathVariable Long produtoId) {

        Produto produto = service.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId);

        if (Objects.isNull(produto))
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .ok(mapper.entityToResponse(produto));
    }

    @PostMapping("/eventos/{eventoId}")
    @Operation(summary = "Cadastrar um novo produto (Evento informado no payload)",
            description = "Cadastra um novo produto. O cabeçalho deve conter o ID da instituição e os dados do produto (incluindo o ID do evento obrigatoriamente) no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou evento não informado")
    })
    public ResponseEntity<ProdutoResponse> createProduto(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "Dados do produto com evento no payload", required = true)
            @RequestBody ProdutoRequest request) {

        Produto produto = mapper.requestToEntity(request);

        Produto produtoCriado = service.create(produto, instituicaoId, eventoId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.entityToResponse(produtoCriado));
    }

    @PutMapping("/eventos/{eventoId}/{produtoId}")
    @Operation(summary = "Atualizar um produto existente",
            description = "Atualiza os dados de um produto pelo ID no path, validando o evento no path e a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para esta instituição neste evento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ProdutoResponse> updateProduto(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do produto a ser atualizado", required = true)
            @PathVariable Long produtoId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody ProdutoRequest request) {

        Produto produtoUpdate = mapper.requestToEntity(request);

        Produto produtoAtualizado = service.update(produtoId, produtoUpdate, instituicaoId, eventoId);

        return ResponseEntity.ok(mapper.entityToResponse(produtoAtualizado));
    }

    @DeleteMapping("/eventos/{eventoId}/{produtoId}")
    @Operation(summary = "Excluir um produto existente",
            description = "Remove definitivamente um produto pelo seu ID se ele pertencer à instituição e ao evento informados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não localizado ou não pertence a esta instituição neste evento")
    })
    public ResponseEntity<Void> deleteProduto(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Long eventoId,
            @Parameter(description = "ID do produto a ser deletado", required = true)
            @PathVariable Long produtoId) {

        service.delete(produtoId, instituicaoId, eventoId);

        return ResponseEntity.noContent().build();
    }
}
