package br.com.rts.eventmanager.seguranca.permissao.services.impl;

import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.permissao.enumerators.AcaoPermissaoEnum;
import br.com.rts.eventmanager.seguranca.permissao.repositories.PermissaoRepository;
import br.com.rts.eventmanager.seguranca.permissao.services.PermissaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.rts.eventmanager.seguranca.permissao.enumerators.AcaoPermissaoEnum.ACESSO;

@Log4j2
@Service
@RequiredArgsConstructor
public class PermissaoServiceImpl implements PermissaoService {

    private final PermissaoRepository repository;

    private static final String TELA = "DASHBOARD";

    @Override
    public Page<Permissao> findAllByTela(String tela, Pageable pageable) {
        return repository.findByTela(tela, pageable);
    }

    @Override
    public Permissao findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permissão não encontrada!"));
    }

    @Override
    public Permissao create(Permissao request) {
        return repository.save(request);
    }

    @Override
    public List<Permissao> findAllById(List<Long> permissaoIds) {
        return repository.findAllById(permissaoIds);
    }

    @Override
    public Set<Permissao> getOrCreateInitialsPermissao() {
        Permissao permissao = this.findByTelaAndAcao(TELA, ACESSO)
                .orElseGet(() -> this.createPermissao(TELA, ACESSO));

        return Set.of(permissao);
    }

    @Override
    public Optional<Permissao> findByTelaAndAcao(String tela, AcaoPermissaoEnum acao) {
        return repository.findByTelaAndAcao(tela, acao);
    }

    @Override
    public Permissao createPermissao(String tela, AcaoPermissaoEnum acao) {
        log.info("Creating new permission: {} - {}", tela, acao);
        return repository.save(Permissao.builder()
                .tela(tela)
                .acao(acao)
                .build());

    }
}

