//package br.com.rts.eventmanager.financeiro.itemvenda.controllers;
//
//import br.com.rts.feijoada_manager.cliente.services.ClienteService;
//import br.com.rts.feijoada_manager.produto.entities.Produto;
//import br.com.rts.feijoada_manager.produto.mappers.ProdutoMapper;
//import br.com.rts.feijoada_manager.produto.services.ProdutoService;
//import br.com.rts.feijoada_manager.util.ReferencedException;
//import br.com.rts.feijoada_manager.util.WebUtils;
//import br.com.rts.feijoada_manager.venda.*;
//import br.com.rts.feijoada_manager.venda.entities.ItemVenda;
//import br.com.rts.feijoada_manager.venda.entities.Venda;
//import br.com.rts.feijoada_manager.venda.mappers.ItemVendaMapper;
//import br.com.rts.feijoada_manager.venda.mappers.VendaMapper;
//import br.com.rts.feijoada_manager.venda.services.ItemVendaService;
//import br.com.rts.feijoada_manager.venda.services.PrinterService;
//import br.com.rts.feijoada_manager.venda.services.VendaService;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.support.SessionStatus;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Base64;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Log4j2
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/itens-venda")
//public class VendaController {
//
//    private final VendaService service;
//    private final VendaMapper mapper;
//
//    private final ItemVendaService itemVendaService;
//    private final ItemVendaMapper itemVendaMapper;
//
//    private final ProdutoService produtoService;
//    private final ProdutoMapper produtoMapper;
//
//    private final PrinterService printerService;
//    private final ClienteService clienteService;
//
//    @Value("${feijoada-manager.vendas.itens-por-pagina:30}")
//    private int pageSize;
//
//
//    @ModelAttribute
//    public void prepareContext(final Model model) {
//        model.addAttribute("formaPagamentoValues", FormaPagamentoEnum.values());
//    }
//
//    @ModelAttribute("venda")
//    public VendaDTO getVendaDTO() {
//        VendaDTO venda = new VendaDTO();
//        venda.setVendido(false);
//        return venda;
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_LISTAGEM')")
//    public String list(
//            @RequestParam(required = false, name = "formaPagamento") final FormaPagamentoEnum formaPagamento,
//            @RequestParam(required = false, name = "vendido") final Boolean vendido,
//            @RequestParam(required = false, name = "valorMin") final BigDecimal valorMin,
//            @RequestParam(required = false, name = "valorMax") final BigDecimal valorMax,
//            @RequestParam(required = false, name = "dataInicio")
//                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataInicio,
//            @RequestParam(required = false, name = "dataFim")
//                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataFim,
//            @RequestParam(required = false, name = "produtoId") final Long produtoId,
//            @RequestParam(defaultValue = "0", name = "page") final int page,
//            final Model model) {
//
//        FiltroVendas filtroVendas =
//                new FiltroVendas(formaPagamento, vendido, valorMin, valorMax, dataInicio, dataFim, produtoId);
//
//        Page<Venda> vendasPage = service.findVendasFiltradas(
//                filtroVendas, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id")));
//
//        List<VendaDTO> vendasDTO = mapper.entityToDTOWithoutItens(vendasPage.getContent());
//
//        VendaSumarioDTO sumario = service.obterSumarioVendas(filtroVendas);
//
//        model.addAttribute("vendas", vendasDTO);
//        model.addAttribute("vendasPage", vendasPage);
//        model.addAttribute("totalValorVendas", sumario.getTotalValor());
//        model.addAttribute("totalItensVendas", sumario.getTotalItens());
//        model.addAttribute("totalVendasFiltradas", sumario.getTotalVendas());
//
//        // Add products list to filter dropdown
//        model.addAttribute("produtos", produtoMapper.entityToDTO(produtoService.findAll()));
//
//        // Pass query params back to keep form inputs populated
//        model.addAttribute("formaPagamento", formaPagamento);
//        model.addAttribute("vendido", vendido);
//        model.addAttribute("valorMin", valorMin);
//        model.addAttribute("valorMax", valorMax);
//        model.addAttribute("dataInicio", dataInicio);
//        model.addAttribute("dataFim", dataFim);
//        model.addAttribute("produtoId", produtoId);
//        model.addAttribute("currentPage", page);
//
//        return "venda/list";
//    }
//
//    @GetMapping("/export")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_LISTAGEM')")
//    public void exportToExcel(
//            @RequestParam(required = false, name = "formaPagamento") final FormaPagamentoEnum formaPagamento,
//            @RequestParam(required = false, name = "vendido") final Boolean vendido,
//            @RequestParam(required = false, name = "valorMin") final BigDecimal valorMin,
//            @RequestParam(required = false, name = "valorMax") final BigDecimal valorMax,
//            @RequestParam(required = false, name = "dataInicio")
//                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataInicio,
//            @RequestParam(required = false, name = "dataFim")
//                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dataFim,
//            @RequestParam(required = false, name = "produtoId") final Long produtoId,
//            final HttpServletResponse response) throws java.io.IOException {
//
//        FiltroVendas filtroVendas =
//                new FiltroVendas(formaPagamento, vendido, valorMin, valorMax, dataInicio, dataFim, produtoId);
//
//        byte[] excelBytes = service.exportToExcel(filtroVendas);
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=vendas_report.xlsx");
//        response.setContentLength(excelBytes.length);
//
//        try (java.io.OutputStream out = response.getOutputStream()) {
//            out.write(excelBytes);
//            out.flush();
//        }
//    }
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
//    @PostMapping("/add-item")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String addItem(@ModelAttribute("venda") final VendaDTO vendaDTO,
//                          @RequestParam("produtoId") final Long produtoId,
//                          @RequestParam("quantidade") final Integer quantidade,
//                          final Model model) {
//
//        ItemVendaDTO existingItem = vendaDTO.getItens().stream()
//                .filter(item -> item.getProdutoId().equals(produtoId))
//                .findFirst()
//                .orElse(null);
//
//        if (existingItem != null) {
//            existingItem.setQuantidade(quantidade);
//        } else {
//            ItemVendaDTO newItem = new ItemVendaDTO();
//            newItem.setProdutoId(produtoId);
//            newItem.setQuantidade(quantidade);
//            vendaDTO.getItens().add(newItem);
//        }
//
//        recalculateTotal(vendaDTO);
//
//        return "venda/add :: summary-card";
//    }
//
//    @PostMapping("/clear-cart")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String clearCart(@ModelAttribute("venda") final VendaDTO vendaDTO, final Model model) {
//        vendaDTO.getItens().clear();
//        vendaDTO.setValorTotal(BigDecimal.ZERO);
//        vendaDTO.setQuantidadeItens(0);
//        return "venda/add :: summary-card";
//    }
//
//    private void recalculateTotal(VendaDTO vendaDTO) {
//        BigDecimal total = BigDecimal.ZERO;
//        int count = 0;
//
//        for (ItemVendaDTO item : vendaDTO.getItens()) {
//            Produto produto = produtoService.findById(item.getProdutoId());
//
//            if (produto != null) {
//                total = total.add(produto.getValorVenda().multiply(new BigDecimal(item.getQuantidade())));
//                count += item.getQuantidade();
//            }
//
//        }
//
//        vendaDTO.setValorTotal(total);
//
//        vendaDTO.setQuantidadeItens(count);
//    }
//
//    @PostMapping("/checkout")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String checkout(@ModelAttribute("venda") @Valid final VendaDTO vendaDTO,
//                           final BindingResult bindingResult, final Model model,
//                           final RedirectAttributes redirectAttributes) {
//        if (vendaDTO.getItens() == null || vendaDTO.getItens().isEmpty()) {
//            bindingResult.rejectValue("itens", "venda.add.emptyCart", "O carrinho não pode estar vazio.");
//        }
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível realizar o checkout pois o formulário esta com erro.");
//            log.error("Não é possível realizar o checkout pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
//            model.addAttribute("produtos", produtoMapper.entityToDTO(produtoService.findAll()));
//            return "venda/add";
//        }
//
//        recalculateTotal(vendaDTO);
//        vendaDTO.setVendido(true);
//
//        // Prepare detailed item list for the checkout view
//        List<Map<String, Object>> displayItens = new java.util.ArrayList<>();
//
//        for (ItemVendaDTO item : vendaDTO.getItens()) {
//            Produto produto = produtoService.findById(item.getProdutoId());
//
//            if (produto != null) {
//                Map<String, Object> map = new java.util.HashMap<>();
//                map.put("produtoId", produto.getId());
//                map.put("nome", produto.getNome());
//                map.put("tamanho", produto.getTamanho());
//                map.put("preco", produto.getValorVenda());
//                map.put("quantidade", item.getQuantidade());
//                map.put("subtotal", produto.getValorVenda().multiply(new BigDecimal(item.getQuantidade())));
//                displayItens.add(map);
//            }
//
//        }
//
//        model.addAttribute("displayItens", displayItens);
//        model.addAttribute("clientes", clienteService.findAll(null, null));
//
//        return "venda/checkout";
//    }
//
//    @PostMapping("/checkout/confirm")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String checkoutConfirm(@ModelAttribute("venda") @Valid final VendaDTO vendaDTO,
//                                  final BindingResult bindingResult,
//                                  @RequestParam(name = "clienteId", required = false) final Long clienteId,
//                                  final Model model,
//                                  final SessionStatus sessionStatus,
//                                  final RedirectAttributes redirectAttributes) {
//
//        if (vendaDTO.getFormaPagamento() == FormaPagamentoEnum.ANOTAR_CONTA && clienteId == null) {
//            bindingResult.rejectValue("formaPagamento", "venda.checkout.clienteObrigatorio", "Para 'Anotar na Conta', selecione um cliente.");
//        }
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não é possível confirmar a venda pois o formulário esta com erro.");
//            log.error("Não é possível confirmar a venda pois o formulário esta com erro. ERRO: {}", bindingResult.getAllErrors());
//
//            // Repopular dados do checkout para exibir os erros amigavelmente
//            List<Map<String, Object>> displayItens = new java.util.ArrayList<>();
//            for (ItemVendaDTO item : vendaDTO.getItens()) {
//                Produto produto = produtoService.findById(item.getProdutoId());
//                if (produto != null) {
//                    Map<String, Object> map = new java.util.HashMap<>();
//                    map.put("produtoId", produto.getId());
//                    map.put("nome", produto.getNome());
//                    map.put("tamanho", produto.getTamanho());
//                    map.put("preco", produto.getValorVenda());
//                    map.put("quantidade", item.getQuantidade());
//                    map.put("subtotal", produto.getValorVenda().multiply(new BigDecimal(item.getQuantidade())));
//                    displayItens.add(map);
//                }
//            }
//            model.addAttribute("displayItens", displayItens);
//            model.addAttribute("clientes", clienteService.findAll(null, null));
//            return "venda/checkout";
//        }
//
//        this.recalculateTotal(vendaDTO);
//
//        Venda venda = service.create(mapper.dtoToEntity(vendaDTO));
//        itemVendaService.saveItensVenda(venda, itemVendaMapper.dtoToEntity(vendaDTO.getItens()));
//
//        if (venda.getFormaPagamento() == FormaPagamentoEnum.ANOTAR_CONTA) {
//            clienteService.anotarVenda(clienteId, venda);
//        }
//
//        List<ItemVenda> savedItens = itemVendaService.findAllByVendaId(venda.getId());
//
//        // Gerar os bytes ESC/POS e codificar em Base64 para envio ao navegador
//        byte[] printBytes = printerService.generateEscPosBytes(venda, savedItens);
//        if (printBytes != null && printBytes.length > 0) {
//            String printPayload = Base64.getEncoder().encodeToString(printBytes);
//            redirectAttributes.addFlashAttribute("printPayload", printPayload);
//        }
//
//        sessionStatus.setComplete();
//
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("venda.create.success"));
//
//        return "redirect:/vendas/add";
//    }
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
//    @PostMapping("/checkout/remove-item")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'VENDAS_CADASTRAR')")
//    public String removeItemCheckout(@ModelAttribute("venda") final VendaDTO vendaDTO,
//                                     @RequestParam("produtoId") final Long produtoId,
//                                     final Model model,
//                                     final RedirectAttributes redirectAttributes) {
//        vendaDTO.getItens().removeIf(item -> item.getProdutoId().equals(produtoId));
//        if (vendaDTO.getItens().isEmpty()) {
//            vendaDTO.setValorTotal(BigDecimal.ZERO);
//            vendaDTO.setQuantidadeItens(0);
//            return "redirect:/vendas/add";
//        }
//        return checkout(vendaDTO, new org.springframework.validation.BeanPropertyBindingResult(vendaDTO, "venda"), model, redirectAttributes);
//    }
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
