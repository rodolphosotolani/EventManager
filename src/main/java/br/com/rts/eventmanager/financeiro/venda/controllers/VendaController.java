package br.com.rts.eventmanager.financeiro.venda.controllers;

import br.com.rts.eventmanager.catalogo.ProdutoDTO;
import br.com.rts.eventmanager.catalogo.ProdutoFacade;
import br.com.rts.eventmanager.catalogo.ServicoDTO;
import br.com.rts.eventmanager.catalogo.ServicoFacade;
import br.com.rts.eventmanager.financeiro.cliente.services.ClienteService;
import br.com.rts.eventmanager.financeiro.ItemVendaDTO;
import br.com.rts.eventmanager.financeiro.VendaDTO;
import br.com.rts.eventmanager.financeiro.VendaSumarioDTO;
import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.enumerators.FormaPagamentoEnum;
import br.com.rts.eventmanager.financeiro.venda.mappers.VendaMapper;
import br.com.rts.eventmanager.financeiro.venda.services.PrinterService;
import br.com.rts.eventmanager.financeiro.venda.services.VendaService;
import br.com.rts.eventmanager.financeiro.venda.specs.FiltroVendas;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService service;
    private final VendaMapper mapper;
    private final PrinterService printerService;

    private final ClienteService clienteService;
    private final ProdutoFacade produtoFacade;
    private final ServicoFacade servicoFacade;

    @Value("${feijoada-manager.vendas.itens-por-pagina:30}")
    private int pageSize;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("formaPagamentoValues", FormaPagamentoEnum.values());
    }

    @ModelAttribute("venda")
    public VendaDTO getVendaDTO() {
        VendaDTO venda = new VendaDTO();
        venda.setVendido(false);
        return venda;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_LISTAR')")
    public String list(HttpSession session,
                       @RequestParam(required = false, name = "formaPagamento") final FormaPagamentoEnum formaPagamento,
                       @RequestParam(required = false, name = "vendido") final Boolean vendido,
                       @RequestParam(required = false, name = "valorMin") final BigDecimal valorMin,
                       @RequestParam(required = false, name = "valorMax") final BigDecimal valorMax,
                       @RequestParam(required = false, name = "dataInicio")
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataInicio,
                       @RequestParam(required = false, name = "dataFim")
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataFim,
                       @RequestParam(required = false, name = "produtoId") final Long produtoId,
                       @RequestParam(defaultValue = "0", name = "page") final int page,
                       Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");
        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        FiltroVendas filtroVendas =
                new FiltroVendas(formaPagamento, vendido, valorMin, valorMax, dataInicio, dataFim, produtoId);

        Page<Venda> vendasPage = service.findVendasFiltradas(
                filtroVendas, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id")));

        List<Venda> vendasDTO = vendasPage.getContent();

        VendaSumarioDTO sumario = service.obterSumarioVendas(filtroVendas);

        model.addAttribute("vendas", vendasDTO);
        model.addAttribute("vendasPage", vendasPage);
        model.addAttribute("totalValorVendas", sumario.totalValor());
        model.addAttribute("totalItensVendas", sumario.totalItens());
        model.addAttribute("totalVendasFiltradas", sumario.totalVendas());

        // Add products list to filter dropdown
        model.addAttribute("produtos", produtoFacade.findAllByInstituicaoAndEvento(tenantId, activeEvId));

        // Pass query params back to keep form inputs populated
        model.addAttribute("formaPagamento", formaPagamento);
        model.addAttribute("vendido", vendido);
        model.addAttribute("valorMin", valorMin);
        model.addAttribute("valorMax", valorMax);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("produtoId", produtoId);
        model.addAttribute("currentPage", page);

//        model.addAttribute("totalFaturamento", totalFaturamento);
//        model.addAttribute("totalVendas", vendas.size());
        model.addAttribute("pageTitle", "Relatório de Vendas");
        return "venda/list";
    }


    @GetMapping("/pdv")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
    public String pdv(HttpSession session,
                      @ModelAttribute("venda") final VendaDTO vendaDTO,
                      RedirectAttributes redirectAttributes,
                      Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Nenhum evento ativo cadastrado! Crie um evento antes de vender.");
            return "redirect:/vendas";
        }

        Venda venda = mapper.dtoToEntity(vendaDTO);
        service.recalculateTotal(venda);
        VendaDTO updated = mapper.entityToDTO(venda);
        populateCartDetails(updated, tenantId, activeEvId);
        vendaDTO.setItens(updated.getItens());
        vendaDTO.setValorTotal(updated.getValorTotal());
        vendaDTO.setQuantidadeItens(updated.getQuantidadeItens());

        Map<Long, Integer> itemQuantities = vendaDTO.getItens()
                .stream()
                .filter(item -> item.getProduto() != null)
                .collect(Collectors.toMap(item -> item.getProduto().getId(), ItemVendaDTO::getQuantidade));
        model.addAttribute("itemQuantities", itemQuantities);

        Map<Long, Integer> itemServicoQuantities = vendaDTO.getItens()
                .stream()
                .filter(item -> item.getServico() != null)
                .collect(Collectors.toMap(item -> item.getServico().getId(), ItemVendaDTO::getQuantidade));
        model.addAttribute("itemServicoQuantities", itemServicoQuantities);

        model.addAttribute("produtos", produtoFacade.findAllByInstituicaoAndEvento(tenantId, activeEvId));
        model.addAttribute("servicos", servicoFacade.findAllByInstituicaoAndEvento(tenantId, activeEvId));
        model.addAttribute("cart", vendaDTO);

        // Obter estatisticas para a bento grid do PDV
        FiltroVendas filtroFaturamento = new FiltroVendas(null, true, null, null, null, null, null);
        VendaSumarioDTO sumarioFaturamento = service.obterSumarioVendas(filtroFaturamento);
        model.addAttribute("totalValorVendas", sumarioFaturamento.totalValor());
        model.addAttribute("totalItensVendas", sumarioFaturamento.totalItens());

        FiltroVendas filtroPendentes = new FiltroVendas(null, false, null, null, null, null, null);
        VendaSumarioDTO sumarioPendentes = service.obterSumarioVendas(filtroPendentes);
        model.addAttribute("totalPendentes", sumarioPendentes.totalVendas());

        model.addAttribute("pageTitle", "Terminal PDV");
        return "venda/pdv";
    }

    @PostMapping("/pdv/add-item")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
    public String addItem(HttpSession session,
                          @ModelAttribute("venda") final VendaDTO vendaDTO,
                          @RequestParam(value = "produtoId", required = false) Long produtoId,
                          @RequestParam(value = "servicoId", required = false) Long servicoId,
                          @RequestParam(value = "quantidade", defaultValue = "1") int quantidade,
                          Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId != null && tenantId != null) {
            Venda venda = mapper.dtoToEntity(vendaDTO);
            venda = service.addItem(venda, produtoId, servicoId, quantidade, tenantId, activeEvId);
            VendaDTO updated = mapper.entityToDTO(venda);
            populateCartDetails(updated, tenantId, activeEvId);
            vendaDTO.setItens(updated.getItens());
            vendaDTO.setValorTotal(updated.getValorTotal());
            vendaDTO.setQuantidadeItens(updated.getQuantidadeItens());
        }

        model.addAttribute("cart", vendaDTO);

        Map<Long, Integer> itemQuantities = vendaDTO.getItens()
                .stream()
                .filter(item -> item.getProduto() != null)
                .collect(Collectors.toMap(item -> item.getProduto().getId(), ItemVendaDTO::getQuantidade));
        model.addAttribute("itemQuantities", itemQuantities);

        Map<Long, Integer> itemServicoQuantities = vendaDTO.getItens()
                .stream()
                .filter(item -> item.getServico() != null)
                .collect(Collectors.toMap(item -> item.getServico().getId(), ItemVendaDTO::getQuantidade));
        model.addAttribute("itemServicoQuantities", itemServicoQuantities);

        if (produtoId != null) {
            model.addAttribute("lastUpdatedProdutoId", produtoId);
        } else if (servicoId != null) {
            model.addAttribute("lastUpdatedServicoId", servicoId);
        }
        model.addAttribute("isHtmx", true);

        return "venda/pdv :: cart-panel";
    }


    @PostMapping("/pdv/clear")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
    public String clearCart(HttpSession session,
                            @ModelAttribute("venda") final VendaDTO vendaDTO,
                            Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        Venda venda = mapper.dtoToEntity(vendaDTO);
        venda = service.clearCart(venda);
        VendaDTO updated = mapper.entityToDTO(venda);
        vendaDTO.setItens(updated.getItens());
        vendaDTO.setValorTotal(updated.getValorTotal());
        vendaDTO.setQuantidadeItens(updated.getQuantidadeItens());

        model.addAttribute("cart", vendaDTO);
        model.addAttribute("cartCleared", true);
        model.addAttribute("produtos", produtoFacade.findAllByInstituicaoAndEvento(tenantId, activeEvId));
        model.addAttribute("servicos", servicoFacade.findAllByInstituicaoAndEvento(tenantId, activeEvId));

        Map<Long, Integer> itemQuantities = java.util.Collections.emptyMap();
        model.addAttribute("itemQuantities", itemQuantities);
        Map<Long, Integer> itemServicoQuantities = java.util.Collections.emptyMap();
        model.addAttribute("itemServicoQuantities", itemServicoQuantities);
        model.addAttribute("isHtmx", true);

        return "venda/pdv :: cart-panel";
    }

    @GetMapping("/checkout")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
    public String checkout(HttpSession session,
                           @ModelAttribute("venda") @Valid final VendaDTO vendaDTO,
                           final BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (vendaDTO.getItens() == null || vendaDTO.getItens().isEmpty()) {
            bindingResult.rejectValue("itens", "venda.add.emptyCart", "O carrinho não pode estar vazio.");
        }
        if (bindingResult.hasErrors()) {
            log.error("Não é possível realizar o checkout pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            model.addAttribute("produtos", produtoFacade.findAllByInstituicaoAndEvento(tenantId, activeEvId));
            model.addAttribute("servicos", servicoFacade.findAllByInstituicaoAndEvento(tenantId, activeEvId));
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "O carrinho está vazio!");
            return "redirect:/vendas/pdv";
        }

        Venda venda = mapper.dtoToEntity(vendaDTO);
        service.recalculateTotal(venda);
        VendaDTO updated = mapper.entityToDTO(venda);
        populateCartDetails(updated, tenantId, activeEvId);
        vendaDTO.setItens(updated.getItens());
        vendaDTO.setValorTotal(updated.getValorTotal());
        vendaDTO.setQuantidadeItens(updated.getQuantidadeItens());
        vendaDTO.setVendido(true);

        model.addAttribute("cart", vendaDTO);
        model.addAttribute("clientes", clienteService.findAllByInstituicao(tenantId));
        model.addAttribute("formasPagamento", FormaPagamentoEnum.values());
        model.addAttribute("pageTitle", "Finalizar Venda");

        return "venda/checkout";
    }

    @PostMapping("/checkout/remove-item")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
    public String removeItemCheckout(HttpSession session,
                                     @ModelAttribute("venda") final VendaDTO vendaDTO,
                                     @RequestParam(value = "produtoId", required = false) final Long produtoId,
                                     @RequestParam(value = "servicoId", required = false) final Long servicoId,
                                     final Model model,
                                     final RedirectAttributes redirectAttributes) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        Venda venda = mapper.dtoToEntity(vendaDTO);
        venda = service.removeItem(venda, produtoId, servicoId);
        VendaDTO updated = mapper.entityToDTO(venda);
        populateCartDetails(updated, tenantId, activeEvId);
        vendaDTO.setItens(updated.getItens());
        vendaDTO.setValorTotal(updated.getValorTotal());
        vendaDTO.setQuantidadeItens(updated.getQuantidadeItens());

        if (vendaDTO.getItens().isEmpty()) {
            vendaDTO.setValorTotal(BigDecimal.ZERO);
            vendaDTO.setQuantidadeItens(0);
            return "redirect:/vendas/pdv";
        }
        return checkout(session, vendaDTO, new BeanPropertyBindingResult(vendaDTO, "venda"), redirectAttributes, model);
    }


    @PostMapping("/checkout/confirm")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
    public String confirmCheckout(HttpSession session,
                                  @ModelAttribute("venda") @Valid final VendaDTO vendaDTO,
                                  @RequestParam(value = "clienteId", required = false) Long clienteId,
                                  final BindingResult bindingResult,
                                  final Model model,
                                  final RedirectAttributes redirectAttributes) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (vendaDTO.getItens() == null || vendaDTO.getItens().isEmpty()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "O carrinho está vazio!");
            return "redirect:/vendas/pdv";
        }
        if (vendaDTO.getFormaPagamento() == FormaPagamentoEnum.ANOTAR_CONTA && clienteId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Selecione um cliente para a opção Anotar na Conta!");
            return "redirect:/vendas/checkout";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível confirmar a venda pois o formulário esta com erro.");
            log.error("Não é possível confirmar a venda pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
            return "redirect:/vendas/checkout";
        }

        // Build Sale
        Venda venda = mapper.dtoToEntity(vendaDTO);
        service.recalculateTotal(venda);

        // Save
        Venda vendaFinalizada = service.create(venda);
        if (vendaFinalizada.getFormaPagamento() == FormaPagamentoEnum.ANOTAR_CONTA) {
            //TODO implementar logica para salvar o registro de Fluxo de caixa
        }


        // Gerar os bytes ESC/POS e codificar em Base64 para envio ao navegador
        byte[] printBytes = printerService.generateEscPosBytes(vendaFinalizada);
        if (printBytes != null && printBytes.length > 0) {
            String printPayload = Base64.getEncoder().encodeToString(printBytes);
            redirectAttributes.addFlashAttribute("printPayload", printPayload);
        }

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Venda realizada com sucesso!");
        return "redirect:/vendas/pdv";
    }


    @GetMapping("/export")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_LISTAR')")
    public void exportToExcel(
            @RequestParam(required = false, name = "formaPagamento") final FormaPagamentoEnum formaPagamento,
            @RequestParam(required = false, name = "vendido") final Boolean vendido,
            @RequestParam(required = false, name = "valorMin") final BigDecimal valorMin,
            @RequestParam(required = false, name = "valorMax") final BigDecimal valorMax,
            @RequestParam(required = false, name = "dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataInicio,
            @RequestParam(required = false, name = "dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataFim,
            @RequestParam(required = false, name = "produtoId") final Long produtoId,
            final HttpServletResponse response) throws java.io.IOException {

        FiltroVendas filtroVendas =
                new FiltroVendas(formaPagamento, vendido, valorMin, valorMax, dataInicio, dataFim, produtoId);

        byte[] excelBytes = service.exportToExcel(filtroVendas);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=vendas_report.xlsx");
        response.setContentLength(excelBytes.length);

        try (java.io.OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
            out.flush();
        }
    }

    private void populateCartDetails(VendaDTO cart, Long tenantId, Long activeEvId) {
        for (ItemVendaDTO item : cart.getItens()) {
            if (item.getProduto() != null && item.getProduto().getId() != null) {
                ProdutoDTO prod = produtoFacade.findByIdAndInstituicaoAndEvento(item.getProduto().getId(), tenantId, activeEvId);
                if (prod != null) {
                    item.setProduto(prod);
                }
            } else if (item.getServico() != null && item.getServico().getId() != null) {
                ServicoDTO srv = servicoFacade.findByIdAndInstituicaoAndEvento(item.getServico().getId(), tenantId, activeEvId);
                if (srv != null) {
                    item.setServico(srv);
                }
            }
        }
    }
}


//
//    @GetMapping("/add")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String add(@ModelAttribute("venda") final VendaDTO vendaDTO, final Model model) {
//        model.addAttribute("produtos", produtoMapper.entityToDTO(produtoService.findAll()));
//
//        Map<Long, Integer> itemQuantities = vendaDTO.getItens()
//                .stream()
//                .collect(Collectors.toMap(ItemVendaDTO::getProdutoId, ItemVendaDTO::getQuantidade));
//        model.addAttribute("itemQuantities", itemQuantities);
//
//        return "venda/add";
//    }
//

//

//

//

//
//    @GetMapping("/checkout/success")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String checkoutSuccess(final Model model) {
//        if (!model.containsAttribute("printVenda")) {
//            return "redirect:/vendas/add";
//        }
//        return "venda/checkout";
//    }
//

//
//    @PostMapping("/checkout/cancel")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String checkoutCancel(@ModelAttribute("venda") final VendaDTO vendaDTO,
//                                 final SessionStatus sessionStatus,
//                                 final RedirectAttributes redirectAttributes) {
//
//        vendaDTO.setVendido(false);
//        this.recalculateTotal(vendaDTO);
//
//        Venda venda = service.create(mapper.dtoToEntity(vendaDTO));
//        itemVendaService.saveItensVenda(venda, itemVendaMapper.dtoToEntity(vendaDTO.getItens()));
//
//        sessionStatus.setComplete();
//
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Compra cancelada e salva com sucesso.");
//
//        return "redirect:/vendas/add";
//    }
//
//    @GetMapping("/edit/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_EDITAR')")
//    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
//
//        model.addAttribute("venda", mapper.entityToDTO(service.findById(id)));
//
//        return "venda/edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_EDITAR')")
//    public String edit(@PathVariable(name = "id") final Long id,
//                       @ModelAttribute("venda") @Valid final VendaDTO vendaDTO,
//                       final BindingResult bindingResult,
//                       final RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível editar a venda pois o formulário esta com erro.");
//            log.error("Não é possível editar a venda pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
//            return "venda/edit";
//        }
//        Venda venda = service.update(id, mapper.dtoToEntity(vendaDTO));
//        itemVendaService.saveItensVenda(venda, itemVendaMapper.dtoToEntity(vendaDTO.getItens()));
//
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("venda.update.success"));
//
//        return "redirect:/vendas";
//    }
//
//    @PostMapping("/delete/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_DELETAR')")
//    public String delete(@PathVariable(name = "id") final Long id,
//                         final RedirectAttributes redirectAttributes) {
//
//        try {
//
//            itemVendaService.deleteByVendaId(id);
//
//            service.delete(id);
//
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("venda.delete.success"));
//
//        } catch (final ReferencedException referencedException) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
//                    referencedException.getKey(), referencedException.getParams().toArray()));
//        }
//
//        return "redirect:/vendas";
//    }
//
//    @PostMapping("/reprint/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_REIMPRIMIR')")
//    @ResponseBody
//    public String reprint(@PathVariable(name = "id") final Long id) {
//        Venda venda = service.findById(id);
//        List<ItemVenda> savedItens = itemVendaService.findAllByVendaId(id);
//        byte[] printBytes = printerService.generateEscPosBytes(venda, savedItens);
//        if (printBytes != null && printBytes.length > 0) {
//            String printPayload = Base64.getEncoder().encodeToString(printBytes);
//            return "<div id=\"print-payload\" hx-swap-oob=\"true\" data-payload=\"" + printPayload + "\"></div>";
//        }
//        return "";
//    }
//
//    @GetMapping("/itens/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_LISTAGEM')")
//    public String getItensVenda(@PathVariable(name = "id") final Long id, final Model model) {
//        List<ItemVenda> items = itemVendaService.findAllByVendaId(id);
//        List<ItemVendaDTO> itemsDTO = itemVendaMapper.entityToDTO(items);
//        model.addAttribute("itens", itemsDTO);
//        model.addAttribute("vendaId", id);
//        return "venda/itens_fragment :: itens-venda-fragment";
//    }
//
//}
