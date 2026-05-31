package br.com.rts.eventmanager.gestao.instituicao.services.impl;

import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import br.com.rts.eventmanager.gestao.instituicao.repositories.InstituicaoRepository;
import br.com.rts.eventmanager.gestao.instituicao.services.InstituicaoService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstituicaoServiceImpl implements InstituicaoService {

    private final InstituicaoRepository repository;

    @Override
    public Page<Instituicao> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<Instituicao> findById(Long instituicaoId) {
        return repository.findById(instituicaoId);
    }

    @Override
    public @Nullable Instituicao create(Instituicao instituicao) {
        return repository.save(instituicao);
    }

    @Override
    public void update(Long id, Instituicao instituicaoNew) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new br.com.rts.eventmanager.utils.NotFoundException("Instituição não encontrada!"));
        instituicao.setNome(instituicaoNew.getNome());
        instituicao.setAtivo(instituicaoNew.getAtivo());
        repository.save(instituicao);
    }

    @Override
    public void delete(Long id) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new br.com.rts.eventmanager.utils.NotFoundException("Instituição não encontrada!"));
        repository.delete(instituicao);
    }

    @Override
    public Boolean existsById(Long instituicaoId) {
        return repository.existsById(instituicaoId);
    }
}
