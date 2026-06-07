package br.com.rts.eventmanager.seguranca.permissao.services;

import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.permissao.enumerators.AcaoPermissaoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissaoService {

    Page<Permissao> findAllByTela(String tela, Pageable pageable);

    Permissao findById(Long id);

    Permissao create(Permissao request);

    List<Permissao> findAllById(List<Long> permissaoIds);

    Set<Permissao> getOrCreateInitialsPermissao();

    Optional<Permissao> findByTelaAndAcao(String tela, AcaoPermissaoEnum acao);

    Permissao createPermissao(String tela, AcaoPermissaoEnum acao);
}

