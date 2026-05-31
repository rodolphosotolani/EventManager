package br.com.rts.eventmanager.gestao.instituicao.services;

import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InstituicaoService {

    Page<Instituicao> findAll(Pageable pageable);

    Optional<Instituicao> findById(Long instituicaoId);

    @Nullable
    Instituicao create(Instituicao instituicao);

    void update(Long id, Instituicao instituicaoNew);

    void delete(Long id);

    Boolean existsById(Long instituicaoId);
}
