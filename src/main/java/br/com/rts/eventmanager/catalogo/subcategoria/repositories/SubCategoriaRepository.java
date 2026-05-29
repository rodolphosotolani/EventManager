package br.com.rts.eventmanager.catalogo.subcategoria.repositories;

import br.com.rts.eventmanager.catalogo.subcategoria.entities.SubCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoriaRepository extends JpaRepository<SubCategoria, Long> {

    SubCategoria findFirstByCategoriaId(Long id);

    List<SubCategoria> findAllByCategoriaId(Long id);

}
