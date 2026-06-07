package br.com.rts.eventmanager.catalogo.categoria.services.impl;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.repositories.CategoriaRepository;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final GestaoFacade gestaoFacade;

    @Override
    public Categoria create(final Long instituicaoId, final Categoria categoriaNew) {

        gestaoFacade.validateIfInstituicaoIsValid(instituicaoId);

        categoriaNew.setInstituicao(instituicaoId);
        return repository.save(categoriaNew);
    }

    @Override
    public Categoria update(final Long instituicaoId, final Long id, final Categoria categoriaNew) {

        gestaoFacade.validateIfInstituicaoIsValid(instituicaoId);

        Categoria categoria = this.findByIdAndInstituicao(id, instituicaoId);
        categoria.setNome(categoriaNew.getNome());
        categoria.setAtivo(categoriaNew.getAtivo());

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
    public List<Categoria> findAllByInstituicao(Long instituicaoId) {
        return repository.findAllByInstituicao(instituicaoId);
    }

    @Override
    public Categoria findByIdAndInstituicao(Long categoriaId, Long instituicaoId) {

        return repository.findByIdAndInstituicao(categoriaId, instituicaoId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada!"));

    }
}
