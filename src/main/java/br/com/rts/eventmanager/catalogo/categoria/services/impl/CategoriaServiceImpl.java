package br.com.rts.eventmanager.catalogo.categoria.services.impl;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.repositories.CategoriaRepository;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final ApplicationEventPublisher publisher;

    @Override
    public List<Categoria> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
    }

    @Override
    public Categoria create(final Long instituicaoId, final Categoria categoriaNew) {
        return repository.save(categoriaNew);
    }

    @Override
    public Categoria update(final Long instituicaoId, final Long id, final Categoria categoriaNew) {
        Categoria categoria = this.findByIdAndInstituicao(id, instituicaoId);
        categoria.setNome(categoriaNew.getNome());
        return repository.save(categoria);
    }

    @Override
    public void delete(final Long instituicaoId, final Long categoriaId) {
        final Categoria categoria = this.findByIdAndInstituicao(categoriaId, instituicaoId);

        repository.delete(categoria);
    }

    @Override
    public @Nullable Page<Categoria> findAllByInstituicao(Long instituicaoId, Pageable pageable) {
        return repository.findAllByInstituicao(instituicaoId, pageable);
    }

    @Override
    public Categoria findByIdAndInstituicao(Long categoriaId, Long instituicaoId) {
        return repository.findByIdAndInstituicao(categoriaId, instituicaoId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada!"));

    }
}
