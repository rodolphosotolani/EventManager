package br.com.rts.eventmanager.seguranca.perfil.services;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;

import java.util.List;
import java.util.Set;

public interface PerfilService {

    Set<Perfil> getOrCreateInitialPerfil(String email);

    Perfil create(Perfil request, List<Long> permissaoIds);

    Perfil get(Long id);

    List<Perfil> listByInstituicao(Long instituicaoId);
}
