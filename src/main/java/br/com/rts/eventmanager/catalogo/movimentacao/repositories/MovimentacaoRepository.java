package br.com.rts.eventmanager.catalogo.movimentacao.repositories;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

}
