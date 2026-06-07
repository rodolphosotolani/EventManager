package br.com.rts.eventmanager.gestao.evento.services.impl;

import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import br.com.rts.eventmanager.gestao.evento.repositories.EventoRepository;
import br.com.rts.eventmanager.gestao.evento.services.EventoService;
import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import br.com.rts.eventmanager.gestao.instituicao.repositories.InstituicaoRepository;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository repository;
    private final InstituicaoRepository instituicaoRepository;

    @Override
    public Page<Evento> findAllByInstituicao(Long instituicaoId, Pageable pageable) {
        return repository.findAllByInstituicaoId(instituicaoId, pageable);
    }

    @Override
    public Optional<Evento> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicaoId(id, instituicaoId);
    }

    @Override
    public Evento create(Long instituicaoId, Evento request) {
        Instituicao instituicao = instituicaoRepository.findById(instituicaoId)
                .orElseThrow(() -> new NotFoundException("Instituição não encontrada!"));
        request.setInstituicao(instituicao);
        return repository.save(request);
    }

    @Override
    public void update(Long id, Long instituicaoId, Evento request) {
        Evento evento = repository.findByIdAndInstituicaoId(id, instituicaoId)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado para esta instituição!"));
        
        evento.setNome(request.getNome());
        evento.setAtivo(request.getAtivo());
        repository.save(evento);
    }

    @Override
    public void delete(Long id, Long instituicaoId) {
        Evento evento = repository.findByIdAndInstituicaoId(id, instituicaoId)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado para esta instituição!"));
        repository.delete(evento);
    }

    @Override
    public Boolean existsByInstituicaoAndId(Long instituicaoId, Long eventoId) {
        return repository.existsByInstituicaoIdAndId(instituicaoId, eventoId);
    }

    @Override
    public List<Evento> findAllByInstituicao(Long instituicaoId) {
        return repository.findAllByInstituicaoId(instituicaoId);
    }
}
