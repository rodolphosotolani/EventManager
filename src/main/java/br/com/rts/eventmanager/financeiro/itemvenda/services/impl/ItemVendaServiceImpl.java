package br.com.rts.eventmanager.financeiro.itemvenda.services.impl;

import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.itemvenda.repositories.ItemVendaRepository;
import br.com.rts.eventmanager.financeiro.itemvenda.services.ItemVendaService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemVendaServiceImpl implements ItemVendaService {

    private final ItemVendaRepository repository;
//    private final InstituicaoRepository instituicaoRepository;
//    private final EventoRepository eventoRepository;

    @Override
    public Page<ItemVenda> findAllByInstituicaoAndEventoAndVendaId(Long instituicaoId, Long eventoId, Long vendaId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEventoAndVendaId(instituicaoId, eventoId, vendaId, pageable);
    }

    @Override
    public Optional<ItemVenda> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(id, instituicaoId, eventoId);
    }

    @Override
    public ItemVenda get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de Venda não encontrado!"));
    }

    @Override
    public ItemVenda create(ItemVenda request, Long instituicaoId) {
//        if (!instituicaoRepository.existsById(request.getInstituicao())) {
//            throw new NotFoundException("Instituição não encontrada!");
//        }
//        if (!eventoRepository.existsById(request.getEvento())) {
//            throw new NotFoundException("Evento não encontrado!");
//        }
        return repository.save(request);
    }

    @Override
    public ItemVenda update(Long id, ItemVenda request, Long instituicaoId) {
        ItemVenda itemVenda = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de Venda não encontrado!"));

        itemVenda.setQuantidade(request.getQuantidade());
        itemVenda.setProduto(request.getProduto());

        return repository.save(itemVenda);
    }

    @Override
    public void delete(Long id) {
        ItemVenda itemVenda = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de Venda não encontrado!"));
        repository.delete(itemVenda);
    }
}
