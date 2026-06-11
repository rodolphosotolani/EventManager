package br.com.rts.eventmanager.gestao.instituicao.services.impl;

import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import br.com.rts.eventmanager.gestao.instituicao.repositories.InstituicaoRepository;
import br.com.rts.eventmanager.gestao.instituicao.services.InstituicaoService;
import br.com.rts.eventmanager.gestao.InstituicaoCriadaEvent;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstituicaoServiceImpl implements InstituicaoService {

    private final InstituicaoRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<Instituicao> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Instituicao> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Instituicao findById(Long instituicaoId) {
        return repository.findById(instituicaoId)
                .orElseThrow(() -> new NotFoundException("Instituição não encontrada!"));
    }

    @Override
    @Transactional
    public @Nullable Instituicao create(Instituicao instituicao) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            throw new AccessDeniedException("Usuário precisa estar autenticado para criar uma instituição.");
        }

        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        }

        if (email == null) {
            throw new AccessDeniedException("Não foi possível identificar o e-mail do usuário autenticado.");
        }

        Instituicao saved = repository.save(instituicao);
        eventPublisher.publishEvent(new InstituicaoCriadaEvent(email, saved.getId()));
        return saved;
    }

    @Override
    public Instituicao update(Long id, Instituicao instituicaoNew) {
        Instituicao instituicao = this.findById(id);

        instituicao.setNome(instituicaoNew.getNome());
        instituicao.setAtivo(instituicaoNew.getAtivo());

        return repository.save(instituicao);
    }

    @Override
    public void delete(Long id) {
        Instituicao instituicao = this.findById(id);

        repository.delete(instituicao);
    }

    @Override
    public Boolean existsById(Long instituicaoId) {
        return repository.existsById(instituicaoId);
    }

    @Override
    public void validateIfIsValid(Long instituicaoId) {
        if (!this.existsById(instituicaoId))
            throw new NotFoundException("Instituição não encontrada!");
    }
}
