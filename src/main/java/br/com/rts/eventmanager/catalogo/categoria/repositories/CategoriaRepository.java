package br.com.rts.eventmanager.catalogo.categoria.repositories;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Nullable
    Page<Categoria> findAllByInstituicao(Long instituicaoId, Pageable pageable);

    @Nullable
    Optional<Categoria> findByIdAndInstituicao(Long categoriaId, Long instituicaoId);
}
