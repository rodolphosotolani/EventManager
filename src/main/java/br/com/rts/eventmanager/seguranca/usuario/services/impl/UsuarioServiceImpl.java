package br.com.rts.eventmanager.seguranca.usuario.services.impl;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.perfil.repositories.PerfilRepository;
import br.com.rts.eventmanager.seguranca.perfil.repositories.PerfilUsuarioRepository;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;
import br.com.rts.eventmanager.seguranca.usuario.repositories.UsuarioInstituicaoRepository;
import br.com.rts.eventmanager.seguranca.usuario.repositories.UsuarioRepository;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioInstituicaoRepository usuarioInstituicaoRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final PerfilRepository perfilRepository;
//    private final EventoRepository eventoRepository;
//    private final InstituicaoRepository instituicaoRepository;

    @Override
    @Transactional
    public Usuario create(Usuario request) {
        if (request.getEmail() != null && repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }
        return repository.save(request);
    }

    @Override
    public Usuario get(Long id) {
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
        Usuario usuario = get(usuarioId);
//        if (!instituicaoRepository.existsById(instituicaoId)) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }

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

        return usuarioInstituicaoRepository.save(link);
    }

    @Override
    @Transactional
    public PerfilUsuario assignPerfilToEvent(Long usuarioId, Long perfilId, Long eventoId) {
        Usuario usuario = get(usuarioId);
        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new NotFoundException("Perfil não encontrado!"));
//        Evento evento = eventoRepository.findById(eventoId)
//                .orElseThrow(() -> new NotFoundException("Evento não encontrado!"));

        // Validar que o perfil pertence à mesma instituição do evento
//        if (!perfil.getInstituicao().equals(evento.getInstituicao().getId())) {
//            throw new IllegalArgumentException("O perfil não pertence à mesma instituição do evento!");
//        }

        // Validar que o usuário está vinculado à instituição do evento
//        if (!usuarioInstituicaoRepository.existsByUsuarioIdAndInstituicao(usuarioId, evento.getInstituicao().getId())) {
//            throw new IllegalArgumentException("O usuário deve primeiro ser vinculado à instituição do evento!");
//        }

        // Verificar duplicados
        if (perfilUsuarioRepository.existsByUsuarioIdAndPerfilIdAndEvento(usuarioId, perfilId, eventoId)) {
            return perfilUsuarioRepository.findAllByUsuarioIdAndEvento(usuarioId, eventoId).stream()
                    .filter(pu -> pu.getPerfil().getId().equals(perfilId))
                    .findFirst()
                    .orElse(null);
        }

        PerfilUsuario perfilUsuario = PerfilUsuario.builder()
                .usuario(usuario)
                .perfil(perfil)
                .evento(eventoId)
                .build();

        return perfilUsuarioRepository.save(perfilUsuario);
    }

    @Override
    public List<Perfil> listPerfisByEvent(Long usuarioId, Long eventoId) {
        get(usuarioId); // Validar usuário
//        if (!eventoRepository.existsById(eventoId)) {
//            throw new NotFoundException("Evento não encontrado!");
//        }
        return perfilUsuarioRepository.findAllByUsuarioIdAndEvento(usuarioId, eventoId).stream()
                .map(PerfilUsuario::getPerfil)
                .collect(Collectors.toList());
    }
}
