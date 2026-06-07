package br.com.rts.eventmanager.financeiro.venda.services;

import br.com.rts.eventmanager.data.VendaSumarioDTO;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.specs.FiltroVendas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Optional;

public interface VendaService {

    Page<Venda> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable);

    Optional<Venda> findByIdAndInstituicao(Long id, Long instituicaoId);

    Optional<Venda> findByIdAndInstituicaoAndEvento(Long id, Long instituicaoId, Long eventoId);

    Page<Venda> findVendasFiltradas(FiltroVendas filtroVendas, Pageable pageable);

    Venda get(Long id);

    Venda create(Venda request);

    Venda update(Long id, Venda request, Long instituicaoId);

    void delete(Long id);

    VendaSumarioDTO obterSumarioVendas(FiltroVendas filtroVendas);

    byte[] exportToExcel(FiltroVendas filtroVendas) throws IOException;

}
