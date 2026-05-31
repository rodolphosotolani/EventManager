package br.com.rts.eventmanager.catalogo.movimentacao.repositories;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    Page<Movimentacao> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Movimentacao findByIdAndInstituicaoAndEvento(Long movimentacaoId, Long instituicaoId, Long eventoId);
}
