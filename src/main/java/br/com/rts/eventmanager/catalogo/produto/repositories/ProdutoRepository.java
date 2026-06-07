package br.com.rts.eventmanager.catalogo.produto.repositories;

import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Page<Produto> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Optional<Produto> findByIdAndInstituicaoAndEvento(Long produtoId, Long instituicaoId, Long eventoId);

    List<Produto> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId);
}
