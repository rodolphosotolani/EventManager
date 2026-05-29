package br.com.rts.eventmanager.catalogo.servico.services.impl;

import br.com.rts.eventmanager.catalogo.estoque.services.EstoqueService;
import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import br.com.rts.eventmanager.catalogo.servico.repositories.ServicoRepository;
import br.com.rts.eventmanager.catalogo.servico.services.ServicoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository repository;
    private final EstoqueService estoqueService;
    private final ApplicationEventPublisher publisher;

    public List<Servico> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
    }

    public Servico findById(final Long servicoId) {
        return repository.findById(servicoId)
                .orElseThrow(() -> new NotFoundException("servico não encontrado!"));
    }

    @Override
    public Long create(Servico servicoNew) {
        return repository.save(servicoNew).getId();
    }

    @Override
    public void update(Long id, Servico servicoNew) {
        Servico servico = this.findById(id);
        servico.setNome(servicoNew.getNome());
        servico.setValorVenda(servicoNew.getValorVenda());
        repository.save(servico);
    }

    public void delete(final Long servicoId) {

        final Servico servico = this.findById(servicoId);

        repository.delete(servico);
    }

    @Override
    public List<Servico> findAllByCategoriaIdAndSubCategoriaId(Long categoriaId, Long subCategoriaId) {
        List<Servico> servicos;
        if (categoriaId != null && subCategoriaId != null) {
            servicos = repository.findAllByCategoriaIdAndSubCategoriaId(categoriaId, subCategoriaId, Sort.by(Sort.Direction.ASC, "nome"));
        } else if (categoriaId != null) {
            servicos = repository.findAllByCategoriaId(categoriaId, Sort.by(Sort.Direction.ASC, "nome"));
        } else {
            servicos = repository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
        }
        return servicos;
    }

}
