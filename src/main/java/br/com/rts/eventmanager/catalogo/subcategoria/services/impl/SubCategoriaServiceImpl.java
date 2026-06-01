package br.com.rts.eventmanager.catalogo.subcategoria.services.impl;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import br.com.rts.eventmanager.catalogo.subcategoria.repositories.SubCategoriaRepository;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SubCategoriaServiceImpl implements SubCategoriaService {

    private final SubCategoriaRepository repository;

    private final GestaoFacade gestaoFacade;
    private final CategoriaService categoriaService;

    @Override
    public Page<SubCategoria> findAllByInstituicaoAndCategoria(Long instituicaoId, Long categoriaId, Pageable pageable) {
        return repository.findAllByInstituicaoAndCategoriaId(instituicaoId, categoriaId, pageable);
    }

    @Override
    public Page<SubCategoria> findAllByInstituicao(Long instituicaoId, Pageable pageable) {
        return repository.findAllByInstituicao(instituicaoId, pageable);
    }

    @Override
    public SubCategoria findByIdAndInstituicao(Long subCategoriaId, Long instituicaoId) {
        return repository.findByIdAndInstituicao(subCategoriaId, instituicaoId)
                .orElseThrow(() -> new NotFoundException("SubCategoria não encontrada"));

    }

    @Override
    public SubCategoria create(SubCategoria subCategoria, Long instituicaoId) {

        gestaoFacade.validateIfInstituicaoIsValid(instituicaoId);

        Categoria categoria = categoriaService.findByIdAndInstituicao(subCategoria.getCategoria().getId(), instituicaoId);

        subCategoria.setCategoria(categoria);
        subCategoria.setInstituicao(instituicaoId);

        return repository.save(subCategoria);
    }

    @Override
    public SubCategoria update(Long subCategoriaId, SubCategoria subCategoriaUpdate, Long instituicaoId) {

        gestaoFacade.validateIfInstituicaoIsValid(instituicaoId);

        final SubCategoria subCategoria = this.findByIdAndInstituicao(subCategoriaId, instituicaoId);

        subCategoria.setNome(subCategoria.getNome());
        subCategoria.setAtivo(subCategoria.getAtivo());

        return repository.save(subCategoria);
    }

    @Override
    public void delete(Long subCategoriaId, Long instituicaoId) {

        SubCategoria subCategoria = this.findByIdAndInstituicao(subCategoriaId, instituicaoId);
        repository.delete(subCategoria);

    }

    @Override
    public void validIfSubCategoriaPertenceCategoria(Long subCategoriaId, Long categoriaId) {

        Boolean exists = repository.existsByIdAndCategoria_Id(subCategoriaId, categoriaId);

        if (!exists)
            throw new NotFoundException("SubCategoria não pertence a esta categoria");
    }
}
