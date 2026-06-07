package br.com.rts.eventmanager.data;

import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioDTO(Long id,
                         String email,
                         String nome,
                         String urlFoto,
                         boolean ativo,
                         LocalDateTime ultimoAcesso,
                         Set<UsuarioInstituicaoDTO> usuarioInstituicaos,
                         Set<PerfilUsuarioDTO> perfilUsuarios) {

}

