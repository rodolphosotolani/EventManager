package br.com.rts.eventmanager.catalogo.estoque.services.impl;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.estoque.repositories.EstoqueRepository;
import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstoqueServiceImpl implements EstoqueService {

    private final EstoqueRepository estoqueRepository;

    @Override
    public Page<Estoque> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return estoqueRepository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Estoque findByIdAndInstituicaoAndEvento(Long estoqueId, Long instituicaoId, Long eventoId) {
        return estoqueRepository.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, eventoId)
                .orElseThrow(() -> new NotFoundException("Estoque não encontrado!"));
    }

    @Override
    public Estoque create(Estoque estoque, Long instituicaoId, Long eventoId) {
        if (estoque.getQuantidadeInicial() == null) {
            estoque.setQuantidadeInicial(estoque.getQuantidadeAtual());
        }
        return estoqueRepository.save(estoque);
    }

    @Override
    public Estoque update(Long estoqueId, Estoque estoqueNew, Long instituicaoId, Long eventoId) {

        Estoque estoque = this.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, eventoId);

        estoque.setQuantidadeAtual(estoqueNew.getQuantidadeAtual());
        estoque.setValorCompraUnitario(estoqueNew.getValorCompraUnitario());

        if (estoqueNew.getProduto() != null) {
            estoque.setProduto(estoqueNew.getProduto());
        }

        return estoqueRepository.save(estoque);
    }

    @Override
    public void delete(Long estoqueId, Long instituicaoId, Long eventoId) {
        Estoque estoque = this.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, eventoId);

        estoqueRepository.delete(estoque);

    }

}
