package br.com.rts.eventmanager;

import br.com.rts.eventmanager.config.TimeZoneConfig;
import br.com.rts.eventmanager.catalogo.ProdutoDTO;
import br.com.rts.eventmanager.catalogo.ProdutoFacade;
import br.com.rts.eventmanager.catalogo.ServicoFacade;
import br.com.rts.eventmanager.financeiro.cliente.services.ClienteService;
import br.com.rts.eventmanager.financeiro.ItemVendaDTO;
import br.com.rts.eventmanager.financeiro.VendaDTO;
import br.com.rts.eventmanager.financeiro.venda.controllers.VendaController;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.venda.mappers.VendaMapper;
import br.com.rts.eventmanager.financeiro.venda.services.PrinterService;
import br.com.rts.eventmanager.financeiro.venda.services.VendaService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EventManagerApplicationTests {

    @Test
    void contextLoads() {
        ApplicationModules.of(EventManagerApplication.class).verify();
    }

    @Test
    void timezoneShouldBeUtcMinusFour() {
        new TimeZoneConfig().init();
        TimeZone tz = TimeZone.getDefault();
        assertThat(tz.getRawOffset()).isEqualTo(-14400000);
    }

    @Test
    void testCheckout() {
        VendaService service = Mockito.mock(VendaService.class);
        VendaMapper mapper = Mockito.mock(VendaMapper.class);
        PrinterService printerService = Mockito.mock(PrinterService.class);
        ClienteService clienteService = Mockito.mock(ClienteService.class);
        ProdutoFacade produtoFacade = Mockito.mock(ProdutoFacade.class);
        ServicoFacade servicoFacade = Mockito.mock(ServicoFacade.class);

        VendaController controller = new VendaController(service, mapper, printerService, clienteService, produtoFacade, servicoFacade);

        HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute("activeInstituicaoId")).thenReturn(1L);
        when(session.getAttribute("activeEventoId")).thenReturn(2L);

        VendaDTO vendaDTO = new VendaDTO();
        ItemVendaDTO item = new ItemVendaDTO();
        ProdutoDTO initialProd = new ProdutoDTO();
        initialProd.setId(10L);
        item.setProduto(initialProd);
        item.setQuantidade(2);
        vendaDTO.setItens(new ArrayList<>(Collections.singletonList(item)));

        Venda vendaEntity = new Venda();
        ItemVenda itemEntity = new ItemVenda();
        itemEntity.setProduto(10L);
        itemEntity.setQuantidade(2);
        vendaEntity.setItens(new ArrayList<>(Collections.singletonList(itemEntity)));

        VendaDTO mappedDTO = new VendaDTO();
        ItemVendaDTO mappedItem = new ItemVendaDTO();
        ProdutoDTO mappedProd = new ProdutoDTO();
        mappedProd.setId(10L);
        mappedItem.setProduto(mappedProd);
        mappedItem.setQuantidade(2);
        mappedDTO.setItens(new ArrayList<>(Collections.singletonList(mappedItem)));

        when(mapper.dtoToEntity(any(VendaDTO.class))).thenReturn(vendaEntity);
        when(mapper.entityToDTO(any(Venda.class))).thenReturn(mappedDTO);

        ProdutoDTO fullyPopulatedProd = new ProdutoDTO();
        fullyPopulatedProd.setId(10L);
        fullyPopulatedProd.setNome("Produto Teste");
        fullyPopulatedProd.setEspecificacao("Especificacao Teste");
        fullyPopulatedProd.setValorVendaUnitario(BigDecimal.TEN);

        when(produtoFacade.findByIdAndInstituicaoAndEvento(10L, 1L, 2L)).thenReturn(fullyPopulatedProd);

        ConcurrentModel model = new ConcurrentModel();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.checkout(session, vendaDTO, new BeanPropertyBindingResult(vendaDTO, "venda"), redirectAttributes, model);

        System.out.println("View: " + view);
        VendaDTO cartInModel = (VendaDTO) model.getAttribute("cart");
        System.out.println("Cart in model: " + cartInModel);
        if (cartInModel != null) {
            System.out.println("Itens size: " + cartInModel.getItens().size());
            for (ItemVendaDTO i : cartInModel.getItens()) {
                System.out.println("Item: qty=" + i.getQuantidade() + ", prod=" + i.getProduto());
                if (i.getProduto() != null) {
                    System.out.println("Produto: id=" + i.getProduto().getId() + ", nome=" + i.getProduto().getNome());
                }
            }
        }
    }
}

