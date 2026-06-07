package br.com.rts.eventmanager.financeiro.cliente.repositories;

import br.com.rts.eventmanager.financeiro.cliente.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findAllByInstituicao(Long instituicao, Pageable pageable);

    Optional<Cliente> findByIdAndInstituicao(Long id, Long instituicao);

    List<Cliente> findAllByInstituicao(Long instituicaoId);
}
