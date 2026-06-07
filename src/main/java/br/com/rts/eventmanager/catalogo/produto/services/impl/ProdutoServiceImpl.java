package br.com.rts.eventmanager.catalogo.produto.services.impl;

import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.repositories.ProdutoRepository;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repository;
    private final GestaoFacade gestaoFacade;
    private final CategoriaService categoriaService;
    private final SubCategoriaService subCategoriaService;

    @Override
    public Page<Produto> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public List<Produto> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId);
    }


    @Override
    public Produto findByIdAndInstituicaoAndEvento(Long produtoId, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
    }

    @Override
    public Produto create(Produto produtoNew, Long instituicaoId) {

        gestaoFacade.validateIfInstituicaoAndEventoIsValid(instituicaoId, produtoNew.getEvento());

        produtoNew.setCategoria(
                categoriaService.findByIdAndInstituicao(produtoNew.getCategoria().getId(), instituicaoId));

        if (Objects.nonNull(produtoNew.getSubCategoria())
                && Objects.nonNull(produtoNew.getSubCategoria().getId())) {

            subCategoriaService.validIfSubCategoriaPertenceCategoria(produtoNew.getSubCategoria().getId(), produtoNew.getCategoria().getId());
            produtoNew.setSubCategoria(
                    subCategoriaService.findByIdAndInstituicao(produtoNew.getSubCategoria().getId(), instituicaoId));
        }

        produtoNew.setInstituicao(instituicaoId);

        return repository.save(produtoNew);
    }

    @Override
    public Produto update(Long produtoId, Produto produtoNew, Long instituicaoId) {

        gestaoFacade.validateIfInstituicaoAndEventoIsValid(instituicaoId, produtoNew.getEvento());

        Produto produto = this.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, produtoNew.getEvento());

        produto.setNome(produtoNew.getNome());
        produto.setEspecificacao(produtoNew.getEspecificacao());
        produto.setValorVendaUnitario(produtoNew.getValorVendaUnitario());
        produto.setQuantidadeMinima(produtoNew.getQuantidadeMinima());

        produtoNew.setCategoria(
                categoriaService.findByIdAndInstituicao(produtoNew.getCategoria().getId(), instituicaoId));

        if (Objects.nonNull(produtoNew.getSubCategoria())
                && Objects.nonNull(produtoNew.getSubCategoria().getId())){

            subCategoriaService.validIfSubCategoriaPertenceCategoria(produtoNew.getSubCategoria().getId(), produtoNew.getCategoria().getId());

            produtoNew.setSubCategoria(
                    subCategoriaService.findByIdAndInstituicao(produtoNew.getSubCategoria().getId(), instituicaoId));
        }

        return repository.save(produto);
    }

    @Override
    public void delete(Long produtoId, Long instituicaoId, Long eventoId) {
        final Produto produto = this.findByIdAndInstituicaoAndEvento(produtoId, instituicaoId, eventoId);

        repository.delete(produto);

    }


}
