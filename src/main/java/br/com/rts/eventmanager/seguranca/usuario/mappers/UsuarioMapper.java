package br.com.rts.eventmanager.seguranca.usuario.mappers;

import br.com.rts.eventmanager.seguranca.usuario.controllers.requests.UsuarioRequest;
import br.com.rts.eventmanager.seguranca.usuario.controllers.responses.UsuarioResponse;
import br.com.rts.eventmanager.seguranca.usuario.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse entityToResponse(Usuario usuario);

    List<UsuarioResponse> entityToResponse(List<Usuario> usuarios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", defaultValue = "true")
    @Mapping(target = "ultimoAcesso", ignore = true)
    @Mapping(target = "usuarioInstituicaos", ignore = true)
    @Mapping(target = "perfilUsuarios", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.OffsetDateTime.now())")
    Usuario requestToEntity(UsuarioRequest request);
}
