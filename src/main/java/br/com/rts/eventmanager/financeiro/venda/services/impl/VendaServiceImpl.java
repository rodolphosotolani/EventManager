package br.com.rts.eventmanager.financeiro.venda.services.impl;

import br.com.rts.eventmanager.catalogo.*;
import br.com.rts.eventmanager.financeiro.VendaSumarioDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendaServiceImpl implements VendaService {

    private final VendaRepository repository;
    private final EntityManager entityManager;

    private final GestaoFacade gestaoFacade;
    private final EstoqueFacade estoqueFacade;
    private final ProdutoFacade produtoFacade;
    private final ServicoFacade servicoFacade;


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
    public Venda create(Venda request, Long instituicaoId, Long eventoId) {

        gestaoFacade.validateIfInstituicaoAndEventoIsValid(instituicaoId, eventoId);

        request.getItens()
                .forEach(itemVenda -> {
                    // Deduct stock levels
                    //TODO substituir por disparo de evento para outro modulo
                    estoqueFacade.subtrairEstoqueProduto(
                            itemVenda.getProduto(),
                            instituicaoId,
                            eventoId,
                            itemVenda.getQuantidade());
                });

        request.setInstituicao(instituicaoId);
        request.setEvento(eventoId);
        request.setVendido(true);
        request.setDataVenda(LocalDateTime.now());

        //TODO Deve registrar uma Movimentacao de estoque
        //TODO Deve registrar um FLuxo de Caixa
        //TODO Deve registrar a Conta do Cliente, caso a forma de Pagamento seja "Anotar na Conta"
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

    @Override
    public Venda addItem(Venda venda, Long produtoId, Long servicoId, int quantidade, Long tenantId, Long activeEvId) {
        ItemVenda existingItem = null;
        if (produtoId != null) {
            existingItem = venda.getItens().stream()
                    .filter(item -> item.getProduto() != null && produtoId.equals(item.getProduto()))
                    .findFirst()
                    .orElse(null);
        } else if (servicoId != null) {
            existingItem = venda.getItens().stream()
                    .filter(item -> item.getServico() != null && servicoId.equals(item.getServico()))
                    .findFirst()
                    .orElse(null);
        }

        if (activeEvId != null && tenantId != null) {
            if (existingItem != null) {
                if (quantidade <= 0) {
                    venda.getItens().remove(existingItem);
                } else {
                    existingItem.setQuantidade(quantidade);
                }
            } else if (quantidade > 0) {
                ItemVenda newItem = new ItemVenda();
                newItem.setInstituicao(tenantId);
                newItem.setEvento(activeEvId);
                newItem.setQuantidade(quantidade);
                newItem.setVenda(venda);
                if (produtoId != null) {
                    newItem.setProduto(produtoId);
                } else if (servicoId != null) {
                    newItem.setServico(servicoId);
                }
                venda.getItens().add(newItem);
            }
        }

        recalculateTotal(venda);
        return venda;
    }

    @Override
    public Venda clearCart(Venda venda) {
        venda.getItens().clear();
        venda.setValorTotal(BigDecimal.ZERO);
        return venda;
    }

    @Override
    public Venda removeItem(Venda venda, Long produtoId, Long servicoId) {
        if (produtoId != null) {
            venda.getItens().removeIf(item -> item.getProduto() != null && produtoId.equals(item.getProduto()));
        } else if (servicoId != null) {
            venda.getItens().removeIf(item -> item.getServico() != null && servicoId.equals(item.getServico()));
        }
        recalculateTotal(venda);
        return venda;
    }

    @Override
    public void recalculateTotal(Venda venda) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() != null) {
                ProdutoDTO produto = produtoFacade.findByIdAndInstituicaoAndEvento(item.getProduto(), item.getInstituicao(), item.getEvento());
                if (produto != null) {
                    total = total.add(produto.getValorVendaUnitario().multiply(new BigDecimal(item.getQuantidade())));
                }
            } else if (item.getServico() != null) {
                ServicoDTO servico = servicoFacade.findByIdAndInstituicaoAndEvento(item.getServico(), item.getInstituicao(), item.getEvento());
                if (servico != null) {
                    total = total.add(servico.getValorVenda().multiply(new BigDecimal(item.getQuantidade())));
                }
            }
        }
        venda.setValorTotal(total);
    }
}
