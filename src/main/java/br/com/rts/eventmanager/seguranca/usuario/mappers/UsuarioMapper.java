package br.com.rts.eventmanager.seguranca.usuario.mappers;

import br.com.rts.eventmanager.seguranca.UsuarioDTO;
import br.com.rts.eventmanager.seguranca.perfilusuario.mappers.PerfilUsuarioMapper;
import br.com.rts.eventmanager.seguranca.usuario.controllers.requests.UsuarioRequest;
import br.com.rts.eventmanager.seguranca.usuario.controllers.responses.UsuarioResponse;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import br.com.rts.eventmanager.seguranca.usuarioinstituicao.mappers.UsuarioInstituicaoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UsuarioInstituicaoMapper.class, PerfilUsuarioMapper.class})
public interface UsuarioMapper {

    UsuarioResponse entityToResponse(Usuario usuario);

    List<UsuarioResponse> entityToResponse(List<Usuario> usuarios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", defaultValue = "true")
    @Mapping(target = "ultimoAcesso", ignore = true)
    @Mapping(target = "usuarioInstituicaos", ignore = true)
    @Mapping(target = "perfilUsuarios", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    Usuario requestToEntity(UsuarioRequest request);


    UsuarioDTO entityToDTO(Usuario usuario);
}
