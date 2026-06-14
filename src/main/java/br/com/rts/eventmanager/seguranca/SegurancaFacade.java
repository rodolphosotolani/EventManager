package br.com.rts.eventmanager.seguranca;

import br.com.rts.eventmanager.seguranca.usuario.mappers.UsuarioMapper;
import br.com.rts.eventmanager.seguranca.usuario.services.UsuarioService;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.services.UsuarioInstituicaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SegurancaFacade {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    private final UsuarioInstituicaoService usuarioInstituicaoService;

    public Optional<UsuarioDTO> findFetchUsuarioByEmail(String email) {
        return usuarioService.findFetchAllByEmail(email)
                .map(usuarioMapper::entityToDTO);
    }

    @Transactional
    public void vincularUsuarioAInstituicao(String email, Long instituicaoId) {
        usuarioService.findByEmail(email).ifPresent(usuario ->
                usuarioInstituicaoService.linkToInstituicao(usuario.getId(), instituicaoId)
        );
    }
}
