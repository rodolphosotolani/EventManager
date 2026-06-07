package br.com.rts.eventmanager.financeiro.venda.utils;

import br.com.rts.eventmanager.financeiro.itemvenda.entities.ItemVenda;
import br.com.rts.eventmanager.financeiro.venda.entities.Venda;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VendaExcelExporter {

    public static byte[] exportToExcel(List<Venda> vendas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Vendas Feijoada");

            // Fonts
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            Font dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short) 10);

            // Custom RGB Terracotta color (FeijoadaManager brand)
            byte[] terracottaRgb = new byte[]{(byte) 168, (byte) 68, (byte) 0}; // #A84400
            XSSFColor terracottaColor = new XSSFColor(terracottaRgb, null);

            // Cell Styles
            XSSFCellStyle headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(terracottaColor);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

            // Data styles
            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setFont(dataFont);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setFont(dataFont);
            textStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setFont(dataFont);
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));
            currencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Row 0: Headers
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28); // Nice tall header row
            String[] columns = {"ID Venda", "Data da Venda", "Forma de Pagamento", "Confirmada", "Quantidade Itens", "Valor Total"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            int totalVendas = vendas.size();
            long totalItens = 0;
            double totalValor = 0.0;

            int rowIdx = 1;
            for (Venda venda : vendas) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20); // Comfortable spacing

                // ID
                Cell cellId = row.createCell(0);
                cellId.setCellValue("#" + venda.getId());
                cellId.setCellStyle(centerStyle);

                // Data Venda
                Cell cellData = row.createCell(1);
                cellData.setCellValue(venda.getDataVenda() != null ? venda.getDataVenda().format(formatter) : "");
                cellData.setCellStyle(centerStyle);

                // Forma de Pagamento
                Cell cellFp = row.createCell(2);
                cellFp.setCellValue(venda.getFormaPagamento() != null ? venda.getFormaPagamento().getDescricao() : "");
                cellFp.setCellStyle(textStyle);

                // Confirmada
                Cell cellConf = row.createCell(3);
                cellConf.setCellValue(venda.getVendido() != null && venda.getVendido() ? "SIM" : "NÃO");
                cellConf.setCellStyle(centerStyle);

                // Quantidade Itens
                Cell cellQtd = row.createCell(4);
                int quantidadeItens = venda.getItens()
                        .stream()
                        .mapToInt(ItemVenda::getQuantidade)
                        .sum();
//                long qtd = venda.getQuantidadeItens() != null ? venda.getQuantidadeItens() : 0;
                cellQtd.setCellValue(quantidadeItens);
                cellQtd.setCellStyle(centerStyle);
                totalItens += quantidadeItens;

                // Valor Total
                Cell cellValor = row.createCell(5);
                double valor = venda.getValorTotal() != null ? venda.getValorTotal().doubleValue() : 0.0;
                cellValor.setCellValue(valor);
                cellValor.setCellStyle(currencyStyle);
                totalValor += valor;
            }

            // Create Totalizer Footer Row
            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.setHeightInPoints(24);

            Font footerFont = workbook.createFont();
            footerFont.setBold(true);
            footerFont.setFontHeightInPoints((short) 10);

            // Light warm terracotta sandy background for footer (slightly darker)
            byte[] footerRgb = new byte[]{(byte) 230, (byte) 210, (byte) 200}; // #E6D2C8
            XSSFColor footerBgColor = new XSSFColor(footerRgb, null);

            XSSFCellStyle footerLabelStyle = (XSSFCellStyle) workbook.createCellStyle();
            footerLabelStyle.setFont(footerFont);
            footerLabelStyle.setFillForegroundColor(footerBgColor);
            footerLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            footerLabelStyle.setAlignment(HorizontalAlignment.CENTER);
            footerLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            footerLabelStyle.setBorderTop(BorderStyle.THIN);
            footerLabelStyle.setBorderBottom(BorderStyle.DOUBLE);

            XSSFCellStyle footerCurrencyStyle = (XSSFCellStyle) workbook.createCellStyle();
            footerCurrencyStyle.setFont(footerFont);
            footerCurrencyStyle.setFillForegroundColor(footerBgColor);
            footerCurrencyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            footerCurrencyStyle.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));
            footerCurrencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            footerCurrencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            footerCurrencyStyle.setBorderTop(BorderStyle.THIN);
            footerCurrencyStyle.setBorderBottom(BorderStyle.DOUBLE);

            // Populate total row cells
            Cell cellLabel = totalRow.createCell(0);
            cellLabel.setCellValue("TOTALIZADORES");
            cellLabel.setCellStyle(footerLabelStyle);

            Cell cellCount = totalRow.createCell(1);
            cellCount.setCellValue(totalVendas + " Vendas");
            cellCount.setCellStyle(footerLabelStyle);

            for (int i = 2; i <= 3; i++) {
                Cell cellBlank = totalRow.createCell(i);
                cellBlank.setCellStyle(footerLabelStyle);
            }

            Cell cellTotalQtd = totalRow.createCell(4);
            cellTotalQtd.setCellValue(totalItens);
            cellTotalQtd.setCellStyle(footerLabelStyle);

            Cell cellTotalValor = totalRow.createCell(5);
            cellTotalValor.setCellValue(totalValor);
            cellTotalValor.setCellStyle(footerCurrencyStyle);

            // Set explicit column widths to bypass java.awt font-manager and freetype library dependencies in headless environments
            sheet.setColumnWidth(0, 18 * 256); // ID Venda
            sheet.setColumnWidth(1, 25 * 256); // Data da Venda
            sheet.setColumnWidth(2, 28 * 256); // Forma de Pagamento
            sheet.setColumnWidth(3, 16 * 256); // Confirmada
            sheet.setColumnWidth(4, 20 * 256); // Quantidade Itens
            sheet.setColumnWidth(5, 22 * 256); // Valor Total

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
