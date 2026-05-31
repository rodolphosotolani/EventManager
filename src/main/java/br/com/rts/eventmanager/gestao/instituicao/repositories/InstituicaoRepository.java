package br.com.rts.eventmanager.gestao.instituicao.repositories;

import br.com.rts.eventmanager.gestao.instituicao.entities.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

}
