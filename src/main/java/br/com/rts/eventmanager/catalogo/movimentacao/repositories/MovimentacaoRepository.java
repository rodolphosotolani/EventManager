package br.com.rts.eventmanager.catalogo.movimentacao.repositories;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("SELECT e FROM Movimentacao e WHERE e.produto.id = :produtoId AND e.quantidade > 0 ORDER BY e.id ASC LIMIT 1")
    Optional<Movimentacao> findFirstByProdutoId(@Param("produtoId") Long produtoId);

    @Query("SELECT SUM(e.quantidade) FROM Movimentacao e WHERE e.produto.id = :produtoId")
    Integer sumQuantidadeByProdutoId(@Param("produtoId") Long produtoId);

    @Query("SELECT AVG(e.valorCompra) FROM Movimentacao e WHERE e.produto.id = :produtoId")
    Double avgValorCompraByProdutoId(@Param("produtoId") Long produtoId);

}
