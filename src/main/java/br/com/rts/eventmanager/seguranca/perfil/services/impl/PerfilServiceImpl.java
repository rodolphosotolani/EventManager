package br.com.rts.eventmanager.seguranca.perfil.services.impl;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.repositories.PerfilRepository;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.permissao.services.PermissaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private final PerfilRepository repository;
    private final PermissaoService permissaoService;

    @Value("${app.security.master-email:}")
    private String masterEmail;

    private static final String ROLE_MASTER = "MASTER";
    private static final String ROLE_BASICO = "BASICO";

    @Override
    public Set<Perfil> getOrCreateInitialPerfil(String email) {

        // Atribuir perfis iniciais
        Set<Perfil> perfis = new HashSet<>();
        if (StringUtils.hasText(masterEmail) && masterEmail.equalsIgnoreCase(email)) {
            log.info("Assigning MASTER role to: {}", email);
            Perfil perfilMaster = repository.findByNome(ROLE_MASTER).orElseGet(() -> this.createPerfil(ROLE_MASTER));
            perfis.add(perfilMaster);
        } else {
            log.info("Assigning BASIC role to: {}", email);
            Perfil perfilBasico = repository.findByNome(ROLE_BASICO).orElseGet(() -> this.createPerfil(ROLE_BASICO));
            perfis.add(perfilBasico);
        }

        return perfis;
    }

    @Override
    @Transactional
    public Perfil create(Perfil request, List<Long> permissaoIds) {

        if (repository.findByNomeAndInstituicao(request.getNome(), request.getInstituicao()).isPresent()) {
            throw new IllegalArgumentException("Perfil com este nome já existe nesta instituição!");
        }

        if (permissaoIds != null && !permissaoIds.isEmpty()) {
            List<Permissao> permissoes = permissaoService.findAllById(permissaoIds);
            request.setPermissoes(new HashSet<>(permissoes));
        }

        return repository.save(request);
    }

    @Override
    public Perfil get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Perfil não encontrado!"));
    }

    @Override
    public List<Perfil> listByInstituicao(Long instituicaoId) {
        return repository.findAllByInstituicao(instituicaoId);
    }

    private Perfil createPerfil(String nomePerfil) {
        Perfil perfil = Perfil
                .builder()
                .nome(nomePerfil)
                .permissoes(permissaoService.getOrCreateInitialsPermissao())
                .build();
        return repository.save(perfil);
    }
}
