package br.com.rts.eventmanager.catalogo.servico.mappers;

import br.com.rts.eventmanager.catalogo.categoria.mappers.CategoriaMapper;
import br.com.rts.eventmanager.catalogo.servico.controllers.requests.ServicoRequest;
import br.com.rts.eventmanager.catalogo.servico.controllers.responses.ServicoResponse;
import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import br.com.rts.eventmanager.catalogo.subcategoria.mappers.SubCategoriaMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {CategoriaMapper.class, SubCategoriaMapper.class})
public abstract class ServicoMapper {

    abstract public ServicoResponse entityToResponse(Servico servico);

    public abstract Servico requestToEntity(ServicoRequest request);

}
