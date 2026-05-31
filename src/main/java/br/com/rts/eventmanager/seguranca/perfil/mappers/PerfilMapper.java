package br.com.rts.eventmanager.seguranca.perfil.mappers;

import br.com.rts.eventmanager.seguranca.permissao.mappers.PermissaoMapper;
import br.com.rts.eventmanager.seguranca.perfil.controllers.responses.PerfilResponse;
import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissaoMapper.class})
public interface PerfilMapper {

    PerfilResponse entityToResponse(Perfil perfil);

    List<PerfilResponse> entityToResponse(List<Perfil> perfis);
}
