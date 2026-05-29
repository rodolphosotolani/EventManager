package br.com.rts.eventmanager.catalogo.produto.repositories;

import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Produto findFirstByCategoriaId(Long categoriaId);

    List<Produto> findAllByCategoriaId(Long id, Sort sort);

    List<Produto> findAllByCategoriaIdAndSubCategoriaId(Long categoriaId, Long subCategoriaId, Sort sort);

    boolean existsByUuid(UUID uuid);

}
