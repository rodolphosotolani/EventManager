package br.com.rts.eventmanager.seguranca.permissao.mappers;

import br.com.rts.eventmanager.seguranca.permissao.controllers.responses.PermissaoResponse;
import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissaoMapper {

    @Mapping(target = "authority", expression = "java(permissao.getAuthority())")
    PermissaoResponse entityToResponse(Permissao permissao);

    List<PermissaoResponse> entityToResponse(List<Permissao> permissoes);

    Set<PermissaoResponse> entityToResponse(Set<Permissao> permissoes);
}
