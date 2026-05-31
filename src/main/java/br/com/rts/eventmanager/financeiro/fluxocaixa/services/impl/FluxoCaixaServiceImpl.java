package br.com.rts.eventmanager.financeiro.fluxocaixa.services.impl;

import br.com.rts.eventmanager.financeiro.caixa.entities.Caixa;
import br.com.rts.eventmanager.financeiro.caixa.repositories.CaixaRepository;
import br.com.rts.eventmanager.financeiro.fluxocaixa.entities.FluxoCaixa;
import br.com.rts.eventmanager.financeiro.fluxocaixa.repositories.FluxoCaixaRepository;
import br.com.rts.eventmanager.financeiro.fluxocaixa.services.FluxoCaixaService;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.repositories.VendaRepository;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FluxoCaixaServiceImpl implements FluxoCaixaService {

    private final FluxoCaixaRepository repository;
//    private final InstituicaoRepository instituicaoRepository;
//    private final EventoRepository eventoRepository;
    private final CaixaRepository caixaRepository;
    private final VendaRepository vendaRepository;

    @Override
    public Page<FluxoCaixa> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Optional<FluxoCaixa> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicao(id, instituicaoId);
    }

    @Override
    public Optional<FluxoCaixa> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(id, instituicaoId, eventoId);
    }

    @Override
    public FluxoCaixa get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fluxo de Caixa não encontrado!"));
    }

    @Override
    public FluxoCaixa create(FluxoCaixa request) {
//        if (!instituicaoRepository.existsById(request.getInstituicao())) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }
//        if (!eventoRepository.existsById(request.getEvento())) {
//            throw new NotFoundException("Evento não encontrado!");
//        }

        if (request.getCaixa() != null && request.getCaixa().getId() != null) {
            Caixa caixa = caixaRepository.findById(request.getCaixa().getId())
                    .orElseThrow(() -> new NotFoundException("Caixa não encontrado!"));
            request.setCaixa(caixa);
        } else {
            request.setCaixa(null);
        }

        if (request.getVenda() != null && request.getVenda().getId() != null) {
            Venda venda = vendaRepository.findById(request.getVenda().getId())
                    .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));
            request.setVenda(venda);
        } else {
            request.setVenda(null);
        }

        return repository.save(request);
    }

    @Override
    public FluxoCaixa update(Long id, FluxoCaixa request, Long instituicaoId) {
        FluxoCaixa fluxoCaixa = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fluxo de Caixa não encontrado!"));

        if (request.getCaixa() != null && request.getCaixa().getId() != null) {
            Caixa caixa = caixaRepository.findById(request.getCaixa().getId())
                    .orElseThrow(() -> new NotFoundException("Caixa não encontrado!"));
            fluxoCaixa.setCaixa(caixa);
        } else {
            fluxoCaixa.setCaixa(null);
        }

        if (request.getVenda() != null && request.getVenda().getId() != null) {
            Venda venda = vendaRepository.findById(request.getVenda().getId())
                    .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));
            fluxoCaixa.setVenda(venda);
        } else {
            fluxoCaixa.setVenda(null);
        }

        fluxoCaixa.setTipoFluxoCaixa(request.getTipoFluxoCaixa());
        fluxoCaixa.setFormaPagamento(request.getFormaPagamento());
        fluxoCaixa.setObservacao(request.getObservacao());
        fluxoCaixa.setDataVenda(request.getDataVenda());
        fluxoCaixa.setUsuario(request.getUsuario());

        return repository.save(fluxoCaixa);
    }

    @Override
    public void delete(Long id) {
        FluxoCaixa fluxoCaixa = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fluxo de Caixa não encontrado!"));
        repository.delete(fluxoCaixa);
    }
}
