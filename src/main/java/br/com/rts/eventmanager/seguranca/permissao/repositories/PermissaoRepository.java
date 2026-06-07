package br.com.rts.eventmanager.seguranca.permissao.repositories;

import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.permissao.enumerators.AcaoPermissaoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

    @Query("SELECT p FROM Permissao p WHERE p.tela = :tela OR :tela IS NULL")
    Page<Permissao> findByTela(String tela, Pageable pageable);

    Optional<Permissao> findByTelaAndAcao(String tela, AcaoPermissaoEnum acao);
}

