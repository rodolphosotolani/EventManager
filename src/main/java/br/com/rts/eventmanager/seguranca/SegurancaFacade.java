package br.com.rts.eventmanager.seguranca;

import br.com.rts.eventmanager.seguranca.usuario.mappers.UsuarioMapper;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SegurancaFacade {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;


    public Optional<UsuarioDTO> findUsuarioByEmail(String email) {
        return usuarioService.findByEmail(email)
                .map(usuarioMapper::entityToDTO);
    }

    @Transactional
    public void vincularUsuarioAInstituicao(String email, Long instituicaoId) {
        usuarioService.findByEmail(email).ifPresent(usuario ->
                usuarioService.linkToInstituicao(usuario.getId(), instituicaoId, true)
        );
    }
}
