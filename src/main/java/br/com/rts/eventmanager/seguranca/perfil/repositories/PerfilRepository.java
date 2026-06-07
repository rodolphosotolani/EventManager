package br.com.rts.eventmanager.seguranca.perfil.repositories;

import br.com.rts.eventmanager.seguranca.perfil.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNome(String nome);

    List<Perfil> findAllByInstituicao(Long instituicao);

    Optional<Perfil> findByIdAndInstituicao(Long id, Long instituicao);

    Optional<Perfil> findByNomeAndInstituicao(String nome, Long instituicao);
}
