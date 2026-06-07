package br.com.rts.eventmanager.seguranca.perfil.mappers;

import br.com.rts.eventmanager.data.PerfilUsuarioDTO;
import br.com.rts.eventmanager.seguranca.perfil.entities.PerfilUsuario;
import br.com.rts.eventmanager.seguranca.permissao.mappers.PermissaoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PermissaoMapper.class})
public interface PerfilUsuarioMapper {

    @Mapping(target = "perfilId", source = "perfil.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    PerfilUsuarioDTO entityToDTO(PerfilUsuario perfilUsuario);
}
