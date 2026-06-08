package br.com.rts.eventmanager.seguranca.usuario.mappers;

import br.com.rts.eventmanager.seguranca.UsuarioInstituicaoDTO;
import br.com.rts.eventmanager.seguranca.usuario.entities.UsuarioInstituicao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioInstituicaoMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    UsuarioInstituicaoDTO entityToDTO(UsuarioInstituicao usuarioInstituicao);
}
