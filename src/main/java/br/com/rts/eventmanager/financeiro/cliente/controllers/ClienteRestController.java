package br.com.rts.eventmanager.financeiro.cliente.controllers;

import br.com.rts.eventmanager.financeiro.cliente.controllers.requests.ClienteRequest;
import br.com.rts.eventmanager.financeiro.cliente.controllers.responses.ClienteResponse;
import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import br.com.rts.eventmanager.financeiro.cliente.mappers.ClienteMapper;
import br.com.rts.eventmanager.financeiro.cliente.services.ClienteService;
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
@RestController("Clientes")
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class ClienteRestController {

    private final ClienteService service;
    private final ClienteMapper mapper;

    @GetMapping()
    @Operation(summary = "Listar todos os clientes da instituição", 
               description = "Retorna uma página contendo todos os clientes cadastrados para a instituição informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum cliente cadastrado para esta instituição")
    })
    public ResponseEntity<Page<ClienteResponse>> getAllClientes(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            Pageable pageable) {

        Page<Cliente> clientes = service.findAllByInstituicao(instituicaoId, pageable);

        if (clientes == null || clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(clientes.map(mapper::entityToResponse));
    }

    @GetMapping("/{clienteId}")
    @Operation(summary = "Obter detalhes de um cliente específico", 
               description = "Retorna os detalhes de um único cliente pelo seu ID se ele pertencer à instituição informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para esta instituição")
    })
    public ResponseEntity<ClienteResponse> getClienteById(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId) {

        Optional<Cliente> clienteOptional = service.findByIdAndInstituicao(clienteId, instituicaoId);

        return clienteOptional
                .map(cliente -> ResponseEntity.ok(mapper.entityToResponse(cliente)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Operation(summary = "Cadastrar um novo cliente", 
               description = "Cadastra um novo cliente no sistema sob a instituição informada no cabeçalho a partir dos dados fornecidos no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    public ResponseEntity<ClienteResponse> create(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "Dados para criação do cliente", required = true)
            @RequestBody ClienteRequest request) {

        Cliente cliente = mapper.requestToEntity(request);

        try {
            Cliente clienteCriado = service.create(instituicaoId, cliente);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.entityToResponse(clienteCriado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{clienteId}")
    @Operation(summary = "Atualizar um cliente existente", 
               description = "Atualiza os dados de um cliente a partir do seu ID e dos novos dados enviados no corpo da requisição, validando a instituição no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para esta instituição"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ClienteResponse> update(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do cliente a ser atualizado", required = true)
            @PathVariable Long clienteId,
            @Parameter(description = "Novos dados para atualização", required = true)
            @RequestBody ClienteRequest request) {

        Optional<Cliente> optionalCliente = service.findByIdAndInstituicao(clienteId, instituicaoId);
        if (optionalCliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cliente clienteUpdate = mapper.requestToEntity(request);
        service.update(clienteId, instituicaoId, clienteUpdate);

        Optional<Cliente> clienteAtualizado = service.findByIdAndInstituicao(clienteId, instituicaoId);
        return clienteAtualizado
                .map(cliente -> ResponseEntity.ok(mapper.entityToResponse(cliente)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{clienteId}")
    @Operation(summary = "Excluir um cliente existente", 
               description = "Remove definitivamente um cliente pelo seu ID se ele pertencer à instituição informada no cabeçalho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para esta instituição")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição dona do catálogo", required = true)
            @RequestHeader("instituicao_id") Long instituicaoId,
            @Parameter(description = "ID do cliente a ser deletado", required = true)
            @PathVariable Long clienteId) {

        Optional<Cliente> optionalCliente = service.findByIdAndInstituicao(clienteId, instituicaoId);
        if (optionalCliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(clienteId, instituicaoId);
        return ResponseEntity.noContent().build();
    }
}
