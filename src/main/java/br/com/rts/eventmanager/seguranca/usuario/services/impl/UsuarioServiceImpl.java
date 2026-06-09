package br.com.rts.eventmanager.seguranca.usuario.services.impl;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.perfil.repositories.PerfilRepository;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;
import br.com.rts.eventmanager.seguranca.usuario.repositories.UsuarioInstituicaoRepository;
import br.com.rts.eventmanager.seguranca.usuario.repositories.UsuarioRepository;
import br.com.rts.eventmanager.seguranca.usuario.services.PerfilUsuarioService;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioInstituicaoRepository usuarioInstituicaoRepository;
    private final PerfilUsuarioService perfilUsuarioService;
    private final PerfilRepository perfilRepository;

    @Override
    @Transactional
    public Usuario create(Usuario request) {
        if (request.getEmail() != null && repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }
        return repository.save(request);
    }

    @Override
    public Usuario createUsuario(String email, String nome, String urlFoto) {

        log.info("New user logging in: {}", email);

        Usuario novoUsuario = repository.save(
                Usuario.builder()
                        .email(email)
                        .nome(nome)
                        .urlFoto(urlFoto)
                        .ativo(true)
                        .ultimoAcesso(LocalDateTime.now())
                        .dateCreated(java.time.LocalDateTime.now())
                        .lastUpdated(java.time.LocalDateTime.now())
                        .build());

        novoUsuario.getPerfilUsuarios().clear();
        novoUsuario.getPerfilUsuarios().addAll(perfilUsuarioService.getOrCreateInitialPerfil(novoUsuario));

        return repository.save(novoUsuario);
    }

    public Usuario getUsuarioById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

    @Override
    public List<Usuario> list() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public UsuarioInstituicao linkToInstituicao(Long usuarioId, Long instituicaoId) {
        Usuario usuario = this.getUsuarioById(usuarioId);

        if (usuarioInstituicaoRepository.existsByUsuarioIdAndInstituicao(usuarioId, instituicaoId)) {
            return usuarioInstituicaoRepository.findAllByUsuarioId(usuarioId).stream()
                    .filter(ui -> ui.getInstituicao().equals(instituicaoId))
                    .findFirst()
                    .orElse(null);
        }

        UsuarioInstituicao link = UsuarioInstituicao.builder()
                .usuario(usuario)
                .instituicao(instituicaoId)
                .build();

        UsuarioInstituicao saved = usuarioInstituicaoRepository.save(link);
        usuario.getUsuarioInstituicaos().add(saved);
        repository.save(usuario);
        return saved;
    }

    @Override
    @Transactional
    public PerfilUsuario assignPerfilToInstituicao(Long usuarioId, Long perfilId, Long instituicaoId) {
        Usuario usuario = this.getUsuarioById(usuarioId);
        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new NotFoundException("Perfil não encontrado!"));

        // Verificar duplicados
        if (perfilUsuarioService.existsByUsuarioIdAndPerfilIdAndInstituicao(usuarioId, perfilId, instituicaoId)) {
            return perfilUsuarioService.findAllByUsuarioIdAndInstituicao(usuarioId, instituicaoId)
                    .stream()
                    .filter(pu -> pu.getPerfil().getId().equals(perfilId))
                    .findFirst()
                    .orElse(null);
        }

        return perfilUsuarioService.create(perfil, usuario, instituicaoId);
    }

    @Override
    public List<Perfil> listPerfisByInstituicao(Long usuarioId, Long instituicaoId) {
        Usuario usuario = this.getUsuarioById(usuarioId);// Validar usuário
        return perfilUsuarioService.findAllByUsuarioIdAndInstituicao(usuario.getId(), instituicaoId)
                .stream()
                .map(PerfilUsuario::getPerfil)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
