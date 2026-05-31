package br.com.rts.eventmanager.financeiro.venda.services.impl;

import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.repositories.VendaRepository;
import br.com.rts.eventmanager.financeiro.venda.services.VendaService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendaServiceImpl implements VendaService {

    private final VendaRepository repository;
//    private final InstituicaoRepository instituicaoRepository;
//    private final EventoRepository eventoRepository;

    @Override
    public Page<Venda> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Optional<Venda> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicao(id, instituicaoId);
    }

    @Override
    public Optional<Venda> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(id, instituicaoId, eventoId);
    }

    @Override
    public Venda get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));
    }

    @Override
    public Venda create(Venda request) {
//        if (!instituicaoRepository.existsById(request.getInstituicao())) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }
//        if (!eventoRepository.existsById(request.getEvento())) {
//            throw new NotFoundException("Evento não encontrado!");
//        }
        return repository.save(request);
    }

    @Override
    public Venda update(Long id, Venda request, Long instituicaoId) {
        Venda venda = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));

        venda.setValorTotal(request.getValorTotal());
        venda.setVendido(request.getVendido());
        venda.setFormaPagamento(request.getFormaPagamento());
        venda.setDataVenda(request.getDataVenda());

        return repository.save(venda);
    }

    @Override
    public void delete(Long id) {
        Venda venda = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));
        repository.delete(venda);
    }
}
