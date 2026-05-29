package br.com.rts.eventmanager.catalogo.categoria.services.impl;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.repositories.CategoriaRepository;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    public List<Categoria> findAll() {
        return categoriaRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
    }

    @Override
    public Categoria findById(final Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada!"));
    }

    @Override
    public Long create(final Categoria categoriaNew) {
        return categoriaRepository.save(categoriaNew).getId();
    }

    @Override
    public void update(final Long id, final Categoria categoriaNew) {
        final Categoria categoria = this.findById(id);
        categoria.setNome(categoriaNew.getNome());
        categoriaRepository.save(categoria);
    }

    @Override
    public void delete(final Long categoriaId) {
        final Categoria categoria = this.findById(categoriaId);

        categoriaRepository.delete(categoria);
    }
}
