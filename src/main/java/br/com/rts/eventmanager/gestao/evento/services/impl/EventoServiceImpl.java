package br.com.rts.eventmanager.gestao.evento.services.impl;

import br.com.rts.eventmanager.gestao.evento.entities.Evento;
import br.com.rts.eventmanager.gestao.evento.repositories.EventoRepository;
import br.com.rts.eventmanager.gestao.evento.services.EventoService;
import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import br.com.rts.eventmanager.gestao.instituicao.repositories.InstituicaoRepository;
import br.com.rts.eventmanager.gestao.instituicao.services.InstituicaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository repository;
    private final InstituicaoService instituicaoService;

    @Override
    public Page<Evento> findAllByInstituicao(Long instituicaoId, Pageable pageable) {
        return repository.findAllByInstituicaoId(instituicaoId, pageable);
    }

    @Override
    public Optional<Evento> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicaoId(id, instituicaoId);
    }

    @Override
    @Transactional
    public Evento create(Long instituicaoId, Evento request) {
        Instituicao instituicao = instituicaoService.findById(instituicaoId);

        // Desativar todos os outros eventos da mesma instituição
        List<Evento> outrosEventos = repository.findAllByInstituicaoId(instituicaoId);
        if (outrosEventos != null) {
            for (Evento e : outrosEventos) {
                e.setAtivo(false);
            }
            repository.saveAll(outrosEventos);
        }

        request.setInstituicao(instituicao);
        request.setAtivo(true);
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

    @Override
    public void validateIfEventoIsValid(Long instituicaoId, Long eventoId) {

        if (!this.existsByInstituicaoAndId(instituicaoId, eventoId))
            throw new NotFoundException("Evento não encontrado!");

    }
}
