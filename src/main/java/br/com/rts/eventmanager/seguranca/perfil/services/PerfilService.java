package br.com.rts.eventmanager.seguranca.perfil.services;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;

import java.util.List;

public interface PerfilService {

    Perfil create(Perfil request, List<Long> permissaoIds);

    Perfil get(Long id);

    List<Perfil> listByInstituicao(Long instituicaoId);
}
