package br.com.rts.eventmanager.financeiro.venda.specs;

import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;

public class VendaSpecification {

    public static Specification<Venda> comFiltros(FiltroVendas filtroVendas) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filtroVendas.formaPagamento() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("formaPagamento"), filtroVendas.formaPagamento()));
            }

            if (filtroVendas.vendido() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("vendido"), filtroVendas.vendido()));
            }

            if (filtroVendas.valorMin() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("valorTotal"), filtroVendas.valorMin()));
            }

            if (filtroVendas.valorMax() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("valorTotal"), filtroVendas.valorMax()));
            }

            if (filtroVendas.dataInicio() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dataVenda"), filtroVendas.dataInicio().atStartOfDay()));
            }

            if (filtroVendas.dataFim() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dataVenda"), filtroVendas.dataFim().atTime(LocalTime.MAX)));
            }

            if (filtroVendas.produtoId() != null) {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<ItemVenda> itemRoot = subquery.from(ItemVenda.class);
                subquery.select(cb.literal(1))
                        .where(cb.equal(itemRoot.get("venda").get("id"), root.get("id")),
                                cb.equal(itemRoot.get("produto").get("id"), filtroVendas.produtoId()));
                predicate = cb.and(predicate, cb.exists(subquery));
            }

            return predicate;
        };
    }
}
