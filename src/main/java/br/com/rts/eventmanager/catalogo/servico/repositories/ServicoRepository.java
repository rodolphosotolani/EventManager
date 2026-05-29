package br.com.rts.eventmanager.catalogo.servico.repositories;

import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    Servico findFirstByCategoriaId(Long categoriaId);

    List<Servico> findAllByCategoriaId(Long id, Sort sort);

    List<Servico> findAllByCategoriaIdAndSubCategoriaId(Long categoriaId, Long subCategoriaId, Sort sort);

    boolean existsByUuid(UUID uuid);

}
