package br.com.rts.eventmanager.seguranca.perfilusuario.services.impl;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.perfilusuario.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.perfilusuario.repositories.PerfilUsuarioRepository;
import br.com.rts.eventmanager.seguranca.perfilusuario.services.PerfilUsuarioService;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UsuarioService usuarioService;

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
    public List<PerfilUsuario> findAllByUsuarioIdAndInstituicao(Long usuarioId, Long instituicaoId) {
        return repository.findAllByUsuarioIdAndInstituicao(usuarioId, instituicaoId);
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

    @Override
    @Transactional
    public void assignPerfilToInstituicao(Long usuarioId, Long instituicaoId, Long perfilId) {

        Usuario usuario = usuarioService.getUsuarioById(usuarioId);

        Perfil perfil = perfilService.get(perfilId);

        // Verificar duplicados
        if (!this.existsByUsuarioIdAndPerfilIdAndInstituicao(usuarioId, perfilId, instituicaoId))
            this.create(perfil, usuario, instituicaoId);

    }

    @Override
    @Transactional
    public void assignPerfilToInstituicao(Long usuarioId, Long instituicaoId, List<Long> perfilIds) {

        Usuario usuario = usuarioService.getUsuarioById(usuarioId);

        perfilIds
                .stream()
                .map(perfilService::get)
                .forEach(perfil -> {
                    // Verificar duplicados
                    if (!this.existsByUsuarioIdAndPerfilIdAndInstituicao(usuarioId, perfil.getId(), instituicaoId))
                        this.create(perfil, usuario, instituicaoId);
                });
    }

}
