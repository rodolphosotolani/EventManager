package br.com.rts.eventmanager.catalogo.subcategoria.services.impl;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import br.com.rts.eventmanager.catalogo.subcategoria.repositories.SubCategoriaRepository;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SubCategoriaServiceImpl implements SubCategoriaService {

    private final SubCategoriaRepository subCategoriaRepository;

    @Override
    public SubCategoria findById(Long subCategoriaId) {
        return subCategoriaRepository
                .findById(subCategoriaId)
                .orElseThrow(() -> new NotFoundException("SubCategoria não encontrada"));
    }

    public List<SubCategoria> findAll() {
        return subCategoriaRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))
                .stream()
                .toList();
    }

    @Override
    public List<SubCategoria> findAllByCategoria(Long categoriaId) {
        return subCategoriaRepository.findAllByCategoriaId(categoriaId)
                .stream()
                .toList();
    }

    @Override
    public Long create(SubCategoria subCategoriaNew) {
        return subCategoriaRepository.save(subCategoriaNew)
                .getId();
    }

    @Override
    public void update(Long id, SubCategoria subCategoriaNew) {
        final SubCategoria subCategoria = this.findById(id);
        subCategoria.setNome(subCategoriaNew.getNome());

        subCategoriaRepository.save(subCategoria);
    }

    @Override
    public void delete(final Long id) {
        subCategoriaRepository.deleteById(id);
    }

//    @EventListener(BeforeDeleteCategoria.class)
//    public void on(final BeforeDeleteCategoria event) {
//        final ReferencedException referencedException = new ReferencedException();
//        final SubCategoria categoriaIdSubCategoria = subCategoriaRepository.findFirstByCategoriaId(event.getId());
//        if (categoriaIdSubCategoria != null) {
//            referencedException.setKey("categoria.subCategoria.categoriaId.referenced");
//            referencedException.addParam(categoriaIdSubCategoria.getNome());
//            throw referencedException;
//        }
//    }
}
