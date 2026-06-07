package br.com.rts.eventmanager.seguranca.usuario.services.impl;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.perfil.repositories.PerfilUsuarioRepository;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.PerfilUsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerfilUsuarioServiceImpl implements PerfilUsuarioService {

    private final PerfilUsuarioRepository repository;
    private final PerfilService perfilService;

    @Override
    public Set<PerfilUsuario> getOrCreateInitialPerfil(Usuario usuario) {

        Set<PerfilUsuario> perfilUsuarioSet = repository.findAllByUsuarioId(usuario.getId());
        if (!perfilUsuarioSet.isEmpty())
            return perfilUsuarioSet;

        return perfilService.getOrCreateInitialPerfil(usuario.getEmail())
                .stream()
                .map(perfil -> this.create(perfil, usuario, null))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean existsByUsuarioIdAndPerfilIdAndInstituicao(Long usuarioId, Long perfilId, Long instituicaoId) {
        return repository.existsByUsuarioIdAndPerfilIdAndInstituicao(usuarioId, perfilId, instituicaoId);
    }

    @Override
    public List<PerfilUsuario> findAllByUsuarioIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findAllByUsuarioIdAndInstituicao(id, instituicaoId);
    }

    @Override
    public PerfilUsuario create(Perfil perfil, Usuario usuario, Long instituicaoId) {
        return repository.save(PerfilUsuario.builder()
                .usuario(usuario)
                .perfil(perfil)
                .instituicao(instituicaoId)
                .dateCreated(LocalDateTime.now())
                .build());
    }
}
