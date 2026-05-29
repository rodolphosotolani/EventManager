package br.com.rts.eventmanager.catalogo.categoria.repositories;

import br.com.rts.eventmanager.catalogo.categoria.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
