package br.com.rts.eventmanager.catalogo.estoque.services.impl;

import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.estoque.repositories.EstoqueRepository;
import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstoqueServiceImpl implements EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoService produtoService;
    private final GestaoFacade gestaoFacade;

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
    public Estoque create(Estoque estoque, Long instituicaoId) {

        gestaoFacade.validateIfInstituicaoAndEventoIsValid(instituicaoId, estoque.getEvento());

        Produto produto =
                produtoService.findByIdAndInstituicaoAndEvento(estoque.getProduto().getId(), instituicaoId, estoque.getEvento());

        estoque.setInstituicao(instituicaoId);
        estoque.setProduto(produto);
        estoque.setQuantidadeInicial(estoque.getQuantidadeAtual());

        return estoqueRepository.save(estoque);
    }

    @Override
    public Estoque updateEstoque(Long estoqueId, Estoque estoqueNew, Long instituicaoId) {

        gestaoFacade.validateIfInstituicaoAndEventoIsValid(instituicaoId, estoqueNew.getEvento());

        Estoque estoque = this.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, estoqueNew.getEvento());

        estoque.setQuantidadeAtual(estoqueNew.getQuantidadeAtual());
        estoque.setValorCompraUnitario(estoqueNew.getValorCompraUnitario());


        return estoqueRepository.save(estoque);
    }

    @Override
    public void delete(Long estoqueId, Long instituicaoId, Long eventoId) {

        Estoque estoque = this.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, eventoId);

        estoqueRepository.delete(estoque);

    }

    @Override
    public Estoque adicionaAoEstoque(Long estoqueId, Estoque estoqueNew, Long instituicaoId) {

        gestaoFacade.validateIfInstituicaoAndEventoIsValid(instituicaoId, estoqueNew.getEvento());

        Estoque estoque = this.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, estoqueNew.getEvento());

        if (estoque.getQuantidadeInicial().equals(estoque.getQuantidadeAtual())) {
            estoque.setQuantidadeInicial(estoque.getQuantidadeInicial() + estoqueNew.getQuantidadeAtual());
            //Fazer a media de valores para adicionar
            estoque.setValorCompraUnitario(estoqueNew.getValorCompraUnitario());
        }
        estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() + estoqueNew.getQuantidadeAtual());


        return estoqueRepository.save(estoque);
    }

    @Override
    public void subtrairEstoqueProduto(Long produtoId, Long instituicaoId, Long eventoId, int quantidade) {
        estoqueRepository.findByProdutoIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId)
                .ifPresent(estoque -> {
                    estoque.setQuantidadeAtual(Math.max(0, estoque.getQuantidadeAtual() - quantidade));
                    estoqueRepository.save(estoque);
                });
    }

    @Override
    public Estoque subtrairDoEstoque(Long estoqueId, Estoque estoqueUpdate, Long instituicaoId) {
        gestaoFacade.validateIfInstituicaoAndEventoIsValid(instituicaoId, estoqueUpdate.getEvento());

        Estoque estoque = this.findByIdAndInstituicaoAndEvento(estoqueId, instituicaoId, estoqueUpdate.getEvento());

        estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() - estoqueUpdate.getQuantidadeAtual());

        return estoqueRepository.save(estoque);
    }

}
