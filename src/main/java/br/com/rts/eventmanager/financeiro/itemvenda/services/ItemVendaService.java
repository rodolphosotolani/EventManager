package br.com.rts.eventmanager.financeiro.itemvenda.services;

import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ItemVendaService {

    Page<ItemVenda> findAllByInstituicaoAndEventoAndVendaId(Long instituicaoId, Long eventoId, Long vendaId, Pageable pageable);

    Optional<ItemVenda> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId);

    ItemVenda get(Long id);

    ItemVenda create(ItemVenda request, Long instituicaoId);

    ItemVenda update(Long id, ItemVenda request, Long instituicaoId);

    void delete(Long id);
}
