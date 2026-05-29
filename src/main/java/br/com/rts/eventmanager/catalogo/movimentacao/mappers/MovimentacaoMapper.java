package br.com.rts.eventmanager.catalogo.movimentacao.mappers;

import br.com.rts.eventmanager.catalogo.movimentacao.dtos.MovimentacaoDTO;
import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public abstract class MovimentacaoMapper {

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto")
    @Mapping(target = "categoriaId", source = "produto.categoria.id")
    @Mapping(target = "categoriaNome", source = "produto.categoria.nome")
    @Mapping(target = "subCategoriaId", source = "produto.subCategoria.id")
    @Mapping(target = "subCategoriaNome", source = "produto.subCategoria.nome")
    abstract public MovimentacaoDTO entityToDTO(Movimentacao movimentacao);

    @Mapping(target = "produto", source = "produtoId")
    @Mapping(target = "estoque", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    abstract public Movimentacao dtoToEntity(MovimentacaoDTO movimentacaoDTO);

    abstract public List<MovimentacaoDTO> dtoToEntity(List<Movimentacao> all);

    public abstract List<MovimentacaoDTO> entityToDTO(List<Movimentacao> all);

}
