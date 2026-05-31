package br.com.rts.eventmanager.seguranca.perfil.services.impl;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import br.com.rts.eventmanager.seguranca.perfil.repositories.PerfilRepository;
import br.com.rts.eventmanager.seguranca.perfil.services.PerfilService;
import br.com.rts.eventmanager.seguranca.permissao.entities.Permissao;
import br.com.rts.eventmanager.seguranca.permissao.repositories.PermissaoRepository;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private final PerfilRepository repository;
    //    private final InstituicaoRepository instituicaoRepository;
    private final PermissaoRepository permissaoRepository;

    @Override
    @Transactional
    public Perfil create(Perfil request, List<Long> permissaoIds) {
//        if (!instituicaoRepository.existsById(request.getInstituicao())) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }

        if (repository.findByNomeAndInstituicao(request.getNome(), request.getInstituicao()).isPresent()) {
            throw new IllegalArgumentException("Perfil com este nome já existe nesta instituição!");
        }

        if (permissaoIds != null && !permissaoIds.isEmpty()) {
            List<Permissao> permissoes = permissaoRepository.findAllById(permissaoIds);
            request.setPermissoes(new HashSet<>(permissoes));
        }

        return repository.save(request);
    }

    @Override
    public Perfil get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Perfil não encontrado!"));
    }

    @Override
    public List<Perfil> listByInstituicao(Long instituicaoId) {
//        if (!instituicaoRepository.existsById(instituicaoId)) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }
        return repository.findAllByInstituicao(instituicaoId);
    }
}
