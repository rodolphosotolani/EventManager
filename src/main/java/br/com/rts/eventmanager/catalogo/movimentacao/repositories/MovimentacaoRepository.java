package br.com.rts.eventmanager.catalogo.movimentacao.repositories;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    Page<Movimentacao> findAllByInstituicaoAndEvento(Long instituicao, Long evento, Pageable pageable);

    Movimentacao findByIdAndInstituicao(Long id, Long instituicao);

    Movimentacao findByIdAndInstituicaoAndEvento(Long id, Long instituicao, Long evento);

}
