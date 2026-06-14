package br.com.rts.eventmanager.seguranca.usuario.services.impl;

import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.repositories.UsuarioRepository;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;

    @Override
    @Transactional
    public Usuario create(Usuario request) {
        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
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

        return repository.save(novoUsuario);
    }

    public Usuario getUsuarioById(Long id) {
        return repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

    @Override
    public List<Usuario> list() {
        return repository.findAll();
    }


    @Override
    public Optional<Usuario> findByEmail(String email) {
        return repository.findAllByEmail(email);
    }

    @Override
    public Optional<Usuario> findFetchAllByEmail(String email) {
        return repository.findFetchAllByEmail(email);
    }
}
