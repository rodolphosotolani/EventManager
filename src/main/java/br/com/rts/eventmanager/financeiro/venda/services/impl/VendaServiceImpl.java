package br.com.rts.eventmanager.financeiro.venda.services.impl;

import br.com.rts.eventmanager.catalogo.EstoqueFacade;
import br.com.rts.eventmanager.data.VendaSumarioDTO;
import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.repositories.VendaRepository;
import br.com.rts.eventmanager.financeiro.venda.services.VendaService;
import br.com.rts.eventmanager.financeiro.venda.specs.FiltroVendas;
import br.com.rts.eventmanager.financeiro.venda.specs.VendaSpecification;
import br.com.rts.eventmanager.financeiro.venda.utils.VendaExcelExporter;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import br.com.rts.eventmanager.utils.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendaServiceImpl implements VendaService {

    private final VendaRepository repository;
    private final EntityManager entityManager;

    private final GestaoFacade gestaoFacade;
    private final EstoqueFacade estoqueFacade;


    @Override
    public Page<Venda> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Optional<Venda> findByIdAndInstituicao(Long id, Long instituicaoId) {
        return repository.findByIdAndInstituicao(id, instituicaoId);
    }

    @Override
    public Optional<Venda> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(id, instituicaoId, eventoId);
    }

    @Override
    public Page<Venda> findVendasFiltradas(FiltroVendas filtroVendas, Pageable pageable) {
        var spec = VendaSpecification.comFiltros(filtroVendas);
        return repository.findAll(spec, pageable);
    }

    public List<Venda> findVendasFiltradas(FiltroVendas filtroVendas) {
        var spec = VendaSpecification.comFiltros(filtroVendas);
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Venda get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));
    }

    @Override
    public Venda create(Venda request) {

        gestaoFacade.validateIfInstituicaoAndEventoIsValid(request.getInstituicao(), request.getEvento());

        request.getItens()
                .forEach(itemVenda -> {
                    // Deduct stock levels
                    //TODO substituir por disparo de evento para outro modulo
                    estoqueFacade.subtrairEstoqueProduto(
                            itemVenda.getProduto(),
                            request.getInstituicao(),
                            request.getInstituicao(),
                            itemVenda.getQuantidade());
                });

        return repository.save(request);
    }

    @Override
    public Venda update(Long id, Venda request, Long instituicaoId) {
        Venda venda = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));

        venda.setValorTotal(request.getValorTotal());
        venda.setVendido(request.getVendido());
        venda.setFormaPagamento(request.getFormaPagamento());
        venda.setDataVenda(request.getDataVenda());

        return repository.save(venda);
    }

    @Override
    public void delete(Long id) {
        Venda venda = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada!"));
        repository.delete(venda);
    }

    @Override
    public VendaSumarioDTO obterSumarioVendas(FiltroVendas filtroVendas) {
        var spec = VendaSpecification.comFiltros(filtroVendas);

        var cb = entityManager.getCriteriaBuilder();

        // 1. Query para somatório do valor total
        var queryValor = cb.createQuery(BigDecimal.class);
        var rootVenda = queryValor.from(Venda.class);
        queryValor.select(cb.coalesce(cb.sum(rootVenda.get("valorTotal")), BigDecimal.ZERO));
        var predicateValor = spec.toPredicate(rootVenda, queryValor, cb);
        if (predicateValor != null) {
            queryValor.where(predicateValor);
        }
        var totalValor = entityManager.createQuery(queryValor).getSingleResult();

        // 2. Query para totalizador de itens vendidos (quantidade acumulada)
        var queryItens = cb.createQuery(Number.class);
        var rootItem = queryItens.from(ItemVenda.class);
        queryItens.select(cb.coalesce(cb.sum(rootItem.get("quantidade")), 0));

        var subquery = queryItens.subquery(Long.class);
        var subVenda = subquery.from(Venda.class);
        subquery.select(subVenda.get("id"));
        var predicateSub = spec.toPredicate(subVenda, queryItens, cb);
        if (predicateSub != null) {
            subquery.where(predicateSub);
        }

        queryItens.where(rootItem.get("venda").get("id").in(subquery));
        var totalItensResult = entityManager.createQuery(queryItens).getSingleResult();
        var totalItens = totalItensResult != null ? totalItensResult.longValue() : 0L;

        // 3. Query para quantidade de vendas total filtrada
        var queryCount = cb.createQuery(Long.class);
        var rootCount = queryCount.from(Venda.class);
        queryCount.select(cb.count(rootCount));
        var predicateCount = spec.toPredicate(rootCount, queryCount, cb);
        if (predicateCount != null) {
            queryCount.where(predicateCount);
        }
        var totalVendas = entityManager.createQuery(queryCount).getSingleResult();

        return new VendaSumarioDTO(totalValor, totalItens, totalVendas);
    }

    @Override
    @Transactional
    public byte[] exportToExcel(FiltroVendas filtroVendas) throws IOException {

        return VendaExcelExporter.exportToExcel(this.findVendasFiltradas(filtroVendas));
    }
}
