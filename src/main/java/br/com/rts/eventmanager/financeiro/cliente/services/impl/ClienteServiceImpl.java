package br.com.rts.eventmanager.financeiro.cliente.services.impl;

import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import br.com.rts.eventmanager.financeiro.cliente.repositories.ClienteRepository;
import br.com.rts.eventmanager.financeiro.cliente.services.ClienteService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
//    private final InstituicaoRepository instituicaoRepository;

    @Override
    public Page<Cliente> findAllByInstituicao(Long instituicaoId, Pageable pageable) {
        return repository.findAllByInstituicao(instituicaoId, pageable);
    }

    @Override
    public Optional<Cliente> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicao(id, instituicaoId);
    }

    @Override
    public Cliente create(Long instituicaoId, Cliente request) {
//        if (!instituicaoRepository.existsById(instituicaoId)) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }
        request.setInstituicao(instituicaoId);
        if (request.getUuid() == null) {
            request.setUuid(UUID.randomUUID());
        }
        return repository.save(request);
    }

    @Override
    public void update(Long id, Long instituicaoId, Cliente request) {
        Cliente cliente = repository.findByIdAndInstituicao(id, instituicaoId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado para esta instituição!"));

        cliente.setNome(request.getNome());
        cliente.setApelido(request.getApelido());
        cliente.setCelular(request.getCelular());
        cliente.setTelefone(request.getTelefone());
        cliente.setEmail(request.getEmail());

        repository.save(cliente);
    }

    @Override
    public void delete(Long id, Long instituicaoId) {
        Cliente cliente = repository.findByIdAndInstituicao(id, instituicaoId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado para esta instituição!"));
        repository.delete(cliente);
    }
}
