package br.com.rts.eventmanager.seguranca.usuarioinstituicao.services.impl;

import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.entities.UsuarioInstituicao;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.repositories.UsuarioInstituicaoRepository;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.services.UsuarioInstituicaoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsuarioInstituicaoServiceImpl implements UsuarioInstituicaoService {

    private final UsuarioInstituicaoRepository repository;

    private final UsuarioService usuarioService;

    @Transactional
    @Override
    public void linkToInstituicao(Long usuarioId, Long instituicaoId) {
        Usuario usuario = usuarioService.getUsuarioById(usuarioId);

        UsuarioInstituicao usuarioInstituicao =
                repository.findByUsuario_IdAndInstituicao(usuarioId, instituicaoId)
                        .orElseGet(() -> this.createLink(usuario, instituicaoId));

        usuarioInstituicao.setAtivo(true);
        usuarioInstituicao.setLastUpdated(LocalDateTime.now());

        repository.save(usuarioInstituicao);
    }

    @Transactional
    @Override
    public void unlinkToInstituicao(Long usuarioId, Long instituicaoId) {

        UsuarioInstituicao usuarioInstituicao =
                repository.findByUsuario_IdAndInstituicao(usuarioId, instituicaoId)
                        .orElseThrow(() -> new NotFoundException("Associação não encontrada!"));

        usuarioInstituicao.setAtivo(false);
        usuarioInstituicao.setLastUpdated(LocalDateTime.now());

        repository.save(usuarioInstituicao);
    }

    private UsuarioInstituicao createLink(Usuario usuario, Long instituicaoId) {
        return UsuarioInstituicao.builder()
                .usuario(usuario)
                .instituicao(instituicaoId)
                .ativo(true)
                .dateCreated(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

}
