package br.com.rts.eventmanager.financeiro.venda.services.impl;

import br.com.rts.eventmanager.catalogo.ProdutoDTO;
import br.com.rts.eventmanager.catalogo.ProdutoFacade;
import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import br.com.rts.eventmanager.financeiro.venda.services.PrinterService;
import br.com.rts.eventmanager.gestao.GestaoFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscPosPrinterService implements PrinterService {

    private final GestaoFacade gestaoFacade;
    private final ProdutoFacade produtoFacade;

    /**
     * Gera os bytes ESC/POS da ficha de venda para serem enviados diretamente pelo navegador (WebUSB / Web Serial).
     */
    @Override
    public byte[] generateEscPosBytes(Venda venda) {
        if (venda == null || venda.getItens().isEmpty()) {
            return new byte[0];
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Determinar o charset de codificação (CP860 para acentuação em português em impressoras térmicas)
            String charset = "CP860";
            try {
                java.nio.charset.Charset.forName(charset);
            } catch (Exception ex) {
                charset = "ISO-8859-1"; // Fallback seguro
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (ItemVenda itemVenda : venda.getItens()) {

                int quantidade = itemVenda.getQuantidade() != null ? itemVenda.getQuantidade() : 1;
                ProdutoDTO produtoDTO =
                        produtoFacade.findByIdAndInstituicaoAndEvento(itemVenda.getProduto(), itemVenda.getInstituicao(), itemVenda.getEvento());

                // Uma ficha por unidade do produto
                for (int i = 0; i < quantidade; i++) {
                    // 1. Inicializar impressora
                    out.write(new byte[]{0x1B, 0x40}); // ESC @

                    // 2. Selecionar tabela de caracteres (ESC t n)
                    // 3 = CP860 (Português)
                    out.write(new byte[]{0x1B, 0x74, 0x03});

                    // 3. Alinhamento Centralizado
                    out.write(new byte[]{0x1B, 0x61, 0x01}); // ESC a 1

                    // 4. Cabeçalho - Título principal (Tamanho Duplo e Negrito)
                    String appTitle = gestaoFacade.getInstituicaoById(venda.getInstituicao()).nome();
                    if (StringUtils.isNoneBlank(appTitle)) {
                        out.write(new byte[]{0x1D, 0x21, 0x11}); // GS ! 17 (Double Width / Double Height)
                        out.write(new byte[]{0x1B, 0x45, 0x01}); // ESC E 1 (Bold on)
                        out.write((appTitle + "\n").getBytes(charset));
                    }

                    // Resetar estilos de texto para o padrão
                    out.write(new byte[]{0x1D, 0x21, 0x00}); // GS ! 0 (Normal size)
                    out.write(new byte[]{0x1B, 0x45, 0x00}); // ESC E 0 (Bold off)

                    // Subtítulo
                    String appSubtitle = gestaoFacade.getEventoById(venda.getInstituicao(), venda.getEvento()).nome();
                    if (StringUtils.isNoneBlank(appSubtitle)) {
                        out.write((appSubtitle + "\n").getBytes(charset));
                        out.write("--------------------------------\n".getBytes(charset));
                        out.write("\n".getBytes("US-ASCII"));
                    }

                    // 5. Nome do Produto (Tamanho Duplo e Negrito)
                    out.write(new byte[]{0x1D, 0x21, 0x11}); // GS ! 17
                    out.write(new byte[]{0x1B, 0x45, 0x01}); // ESC E 1
                    String nomeProduto = produtoDTO.getNome().toUpperCase() + "\n";
                    out.write(nomeProduto.getBytes(charset));

                    // Resetar estilos
                    out.write(new byte[]{0x1D, 0x21, 0x00});
                    out.write(new byte[]{0x1B, 0x45, 0x00});

                    // Tamanho (se houver)
                    if (produtoDTO.getEspecificacao() != null && !produtoDTO.getEspecificacao().trim().isEmpty()) {
                        out.write(("Tamanho: " + produtoDTO.getEspecificacao() + "\n").getBytes(charset));
                    }

                    // Data e hora da venda
                    if (venda.getDataVenda() != null) {
                        out.write((venda.getDataVenda().format(formatter) + "\n").getBytes(charset));
                    }

                    // 6. Valor Unitário (Negrito)
                    out.write(new byte[]{0x1B, 0x45, 0x01}); // ESC E 1
                    String valorStr = String.format("VALOR: R$ %,.2f\n", produtoDTO.getValorVendaUnitario());
                    out.write(valorStr.getBytes(charset));
                    out.write(new byte[]{0x1B, 0x45, 0x00}); // ESC E 0

                    out.write("--------------------------------\n".getBytes(charset));
                    out.write("\n".getBytes("US-ASCII"));

                    // 7. Código de Barras (Nativo ESC/POS CODE39)
                    // Valor: vendaId-itemId-produtoId
                    String barcodeValue = venda.getInstituicao() + "-" + venda.getEvento() + "-" +
                            venda.getId() + "-" + itemVenda.getId() + "-" + produtoDTO.getId();

                    // Configurações do código de barras:
                    out.write(new byte[]{0x1D, 0x68, 80}); // Altura: 60 dots
                    out.write(new byte[]{0x1D, 0x77, 2});  // Largura (multiplicador): 2
//                    out.write(new byte[]{0x1D, 0x48, 2});  // HRI: Imprimir texto abaixo do código
//                    out.write(new byte[]{0x1D, 0x66, 0});  // Fonte HRI: Font A

                    // Imprimir código de barras CODE39 (Tipo 69 no sistema B)
                    out.write(new byte[]{0x1D, 0x6B, 69, (byte) barcodeValue.length()});
                    out.write(barcodeValue.getBytes("US-ASCII"));

                    // 8. Espaçamento final e corte de papel parcial/total
//                    out.write("\n\n".getBytes("US-ASCII"));
                    out.write(new byte[]{0x1D, 0x56, 0x42, 0x00}); // Comando de corte (GS V 66 0)
                }
            }

            return out.toByteArray();

        } catch (Exception e) {
            log.error("Falha ao gerar bytes ESC/POS para venda ID {}", venda.getId(), e);
            return new byte[0];
        }
    }
}
