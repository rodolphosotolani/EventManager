package br.com.rts.eventmanager.catalogo.servico.repositories;

import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findAllByCategoriaId(Long id, Sort sort);

    List<Servico> findAllByCategoriaIdAndSubCategoriaId(Long categoriaId, Long subCategoriaId, Sort sort);

}
