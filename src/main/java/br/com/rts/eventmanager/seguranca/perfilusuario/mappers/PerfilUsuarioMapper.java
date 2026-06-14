package br.com.rts.eventmanager.seguranca.perfilusuario.mappers;

import br.com.rts.eventmanager.seguranca.PerfilUsuarioDTO;
import br.com.rts.eventmanager.seguranca.perfilusuario.controllers.responses.PerfilUsuarioResponse;
import br.com.rts.eventmanager.seguranca.perfilusuario.entities.PerfilUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PerfilUsuarioMapper {

    @Mapping(target = "perfilId", source = "perfil.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    PerfilUsuarioDTO entityToDTO(PerfilUsuario perfilUsuario);

    @Mapping(target = "perfilId", source = "perfil.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    PerfilUsuarioResponse entityToResponse(PerfilUsuario perfilUsuarios);

    List<PerfilUsuarioResponse> entityToResponse(List<PerfilUsuario> perfilUsuarios);
}
