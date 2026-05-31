package br.com.rts.eventmanager.catalogo.subcategoria.repositories;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubCategoriaRepository extends JpaRepository<SubCategoria, Long> {

    Page<SubCategoria> findAllByInstituicao(Long instituicaoId, Pageable pageable);

    Optional<SubCategoria> findByIdAndInstituicao(Long id, Long instituicaoId);

    Page<SubCategoria> findAllByInstituicaoAndCategoriaId(Long instituicaoId,
                                                           Long categoriaId,
                                                           Pageable pageable);

}
