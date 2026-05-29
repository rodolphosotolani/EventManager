package br.com.rts.eventmanager.catalogo.estoque.repositories;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    @Query("SELECT SUM(e.quantidadeAtual) FROM Estoque e WHERE e.produto.id = :produtoId")
    Integer sumQuantidadeByProdutoId(@Param("produtoId") Long produtoId);

}
