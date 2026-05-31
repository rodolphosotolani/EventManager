package br.com.rts.eventmanager.financeiro.conta.services.impl;

import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import br.com.rts.eventmanager.financeiro.cliente.repositories.ClienteRepository;
import br.com.rts.eventmanager.financeiro.conta.entities.Conta;
import br.com.rts.eventmanager.financeiro.conta.repositories.ContaRepository;
import br.com.rts.eventmanager.financeiro.conta.services.ContaService;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.repositories.VendaRepository;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository repository;
    //    private final InstituicaoRepository instituicaoRepository;
//    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final VendaRepository vendaRepository;

    @Override
    public Page<Conta> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Optional<Conta> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicao(id, instituicaoId);
    }

    @Override
    public Optional<Conta> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(id, instituicaoId, eventoId);
    }

    @Override
    public Conta get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada!"));
    }

    @Override
    public Conta create(Conta request, Long instituicaoId) {
//        if (!instituicaoRepository.existsById(request.getInstituicao())) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }
//        if (!eventoRepository.existsById(request.getEvento())) {
//            throw new NotFoundException("Evento não encontrado!");
//        }

        Cliente cliente = clienteRepository.findById(request.getCliente().getId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));

        Venda venda = vendaRepository.findById(request.getVenda().getId())
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));

        request.setCliente(cliente);
        request.setVenda(venda);

        if (request.getUuid() == null) {
            request.setUuid(UUID.randomUUID());
        }
        return repository.save(request);
    }

    @Override
    public Conta update(Long id, Conta request, Long instituicaoId) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada!"));

        Cliente cliente = clienteRepository.findById(request.getCliente().getId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));

        Venda venda = vendaRepository.findById(request.getVenda().getId())
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));

        conta.setCliente(cliente);
        conta.setVenda(venda);
        conta.setSaldoDevedor(request.getSaldoDevedor());
        conta.setPago(request.getPago());
        conta.setDataPagamento(request.getDataPagamento());

        return repository.save(conta);
    }

    @Override
    public void delete(Long id) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada!"));
        repository.delete(conta);
    }
}
