package br.com.rts.eventmanager.catalogo.servico.repositories;

import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

}
