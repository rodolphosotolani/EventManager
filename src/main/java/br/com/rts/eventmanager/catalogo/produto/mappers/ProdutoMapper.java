package br.com.rts.eventmanager.catalogo.produto.mappers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.produto.dtos.ProdutoDTO;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoriaMapper.class, SubCategoriaMapper.class})
public abstract class ProdutoMapper {

    @Autowired
    ProdutoService produtoService;

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNome", source = "categoria.nome")
    @Mapping(target = "subCategoriaId", source = "subCategoria.id")
    @Mapping(target = "subCategoriaNome", source = "subCategoria.nome")
    @Mapping(target = "quantidadeEstoque", source = "produto", qualifiedByName = "quantidadeEstoqueByProduto")
    abstract public ProdutoDTO entityToDTO(Produto produto);

    @Mapping(target = "uuid", defaultExpression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "categoria", source = "categoriaId")
    @Mapping(target = "subCategoria", source = "subCategoriaId")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "estoques", ignore = true)
    abstract public Produto dtoToEntity(ProdutoDTO produtoDTO);

    public Produto idToEntity(Long produtoId) {
        return produtoService.findById(produtoId);
    }

    @Named("quantidadeEstoqueByProduto")
    public Integer quantidadeEstoqueByProduto(Produto produto) {
        return produtoService.quantidadeEstoqueProduto(produto);
    }

    public String getProdutoNome(Produto produto) {
        return produto.getNome() + " - " + produto.getEspecificacao();
    }

    public abstract List<ProdutoDTO> entityToDTO(List<Produto> all);
}
