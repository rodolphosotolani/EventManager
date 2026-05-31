package br.com.rts.eventmanager.financeiro.caixa.services.impl;

import br.com.rts.eventmanager.financeiro.caixa.entities.Caixa;
import br.com.rts.eventmanager.financeiro.caixa.repositories.CaixaRepository;
import br.com.rts.eventmanager.financeiro.caixa.services.CaixaService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaixaServiceImpl implements CaixaService {

    private final CaixaRepository repository;
//    private final InstituicaoRepository instituicaoRepository;
//    private final EventoRepository eventoRepository;

    @Override
    public Page<Caixa> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Optional<Caixa> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicao(id, instituicaoId);
    }

    @Override
    public Optional<Caixa> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(id, instituicaoId, eventoId);
    }

    @Override
    public Caixa get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Caixa não encontrado!"));
    }

    @Override
    public Caixa create(Caixa request, Long instituicaoId) {
//        if (!instituicaoRepository.existsById(request.getInstituicao())) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }
//        if (!eventoRepository.existsById(request.getEvento())) {
//            throw new NotFoundException("Evento não encontrado!");
//        }

        if (request.getUuid() == null) {
            request.setUuid(UUID.randomUUID());
        }
        return repository.save(request);
    }

    @Override
    public Caixa update(Long id, Caixa request, Long instituicaoId) {
        Caixa caixa = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Caixa não encontrado!"));

        caixa.setUsuarioAbertura(request.getUsuarioAbertura());
        caixa.setUsuarioFechamento(request.getUsuarioFechamento());
        caixa.setStatusCaixa(request.getStatusCaixa());
        caixa.setDataAbertura(request.getDataAbertura());
        caixa.setDataFechamento(request.getDataFechamento());
        caixa.setSaldoInicial(request.getSaldoInicial());
        caixa.setSaldoFinalCalculado(request.getSaldoFinalCalculado());
        caixa.setSaldoFinalInformado(request.getSaldoFinalInformado());
        caixa.setDiferencaCaixa(request.getDiferencaCaixa());

        return repository.save(caixa);
    }

    @Override
    public void delete(Long id) {
        Caixa caixa = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Caixa não encontrado!"));
        repository.delete(caixa);
    }
}
