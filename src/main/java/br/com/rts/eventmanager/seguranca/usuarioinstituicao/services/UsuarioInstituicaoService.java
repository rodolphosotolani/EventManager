package br.com.rts.eventmanager.seguranca.usuarioinstituicao.services;

public interface UsuarioInstituicaoService {

    void linkToInstituicao(Long usuarioId, Long instituicaoId);

    void unlinkToInstituicao(Long usuarioId, Long instituicaoId);
}
