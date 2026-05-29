package br.com.rts.eventmanager.catalogo.estoque.mappers;

import br.com.rts.eventmanager.catalogo.estoque.dtos.EstoqueDTO;
import br.com.rts.eventmanager.catalogo.estoque.entities.Estoque;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public abstract class EstoqueMapper {

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto")
    @Mapping(target = "categoriaId", source = "produto.categoria.id")
    @Mapping(target = "categoriaNome", source = "produto.categoria.nome")
    @Mapping(target = "subCategoriaId", source = "produto.subCategoria.id")
    @Mapping(target = "subCategoriaNome", source = "produto.subCategoria.nome")
    abstract public EstoqueDTO entityToDTO(Estoque estoque);

    @Mapping(target = "produto", source = "produtoId")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "movimentacoes", ignore = true)
    abstract public Estoque dtoToEntity(EstoqueDTO estoqueDTO);

    abstract public List<EstoqueDTO> dtoToEntity(List<Estoque> all);

    public abstract List<EstoqueDTO> entityToDTO(List<Estoque> all);

}
