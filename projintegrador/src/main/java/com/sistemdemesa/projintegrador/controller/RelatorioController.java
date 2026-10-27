package com.sistemdemesa.projintegrador.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.sistemdemesa.projintegrador.entity.Pedido;
import com.sistemdemesa.projintegrador.entity.Produto;
import com.sistemdemesa.projintegrador.repository.PedidoRepository;
import com.sistemdemesa.projintegrador.repository.ProdutoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/home/relatorios")
public class RelatorioController {
    private static final Color BRAND = new Color(93, 21, 21);
    private static final Color LIGHT_ROW = new Color(250, 246, 246);
    private static final Color BORDER = new Color(230, 220, 220);
    private static final Locale BRAZIL = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", BRAZIL);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", BRAZIL);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", BRAZIL);

    private final ProdutoRepository produtos;
    private final PedidoRepository pedidos;

    public RelatorioController(ProdutoRepository produtos, PedidoRepository pedidos) {
        this.produtos = produtos;
        this.pedidos = pedidos;
    }

    @GetMapping
    String pagina() {
        return "app/relatorio/relatorio";
    }

    @GetMapping("/dashboard")
    String dash() {
        return "app/relatorio/dashboard";
    }

    @GetMapping("/exportar/produtos")
    ResponseEntity<byte[]> produtosPdf() throws Exception {
        List<Produto> lista = produtos.findAll().stream()
                .sorted(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
        List<List<String>> rows = lista.stream()
                .map(produto -> List.of(
                        String.valueOf(produto.getId()),
                        texto(produto.getNome()),
                        texto(produto.getCategoria()),
                        moeda(produto.getPrecoVenda()),
                        produto.getQuantidade() == null ? "—" : produto.getQuantidade().toString()))
                .toList();

        return pdf(
                "Relatório de produtos",
                "Produtos cadastrados: " + lista.size(),
                List.of("ID", "Produto", "Categoria", "Preço de venda", "Estoque"),
                rows,
                "Nenhum produto cadastrado.",
                new float[]{0.7f, 2.5f, 1.5f, 1.4f, 0.8f},
                List.of(0, 3, 4),
                "relatorio-produtos.pdf");
    }

    @GetMapping("/exportar/vendasdia")
    ResponseEntity<byte[]> dia() throws Exception {
        LocalDate hoje = LocalDate.now();
        List<Pedido> vendas = pedidos.findAll().stream()
                .filter(pedido -> "concluido".equals(pedido.getStatus())
                        && pedido.getCreatedAt() != null
                        && pedido.getCreatedAt().toLocalDate().equals(hoje))
                .sorted(Comparator.comparing(Pedido::getCreatedAt).reversed())
                .toList();
        return vendasPdf("Vendas do dia", "Período: " + DATE.format(hoje), vendas, "relatorio-vendas-dia.pdf");
    }

    @GetMapping("/exportar/vendasmes")
    ResponseEntity<byte[]> mes() throws Exception {
        YearMonth mesAtual = YearMonth.now();
        List<Pedido> vendas = pedidos.findAll().stream()
                .filter(pedido -> "concluido".equals(pedido.getStatus())
                        && pedido.getCreatedAt() != null
                        && YearMonth.from(pedido.getCreatedAt()).equals(mesAtual))
                .sorted(Comparator.comparing(Pedido::getCreatedAt).reversed())
                .toList();
        return vendasPdf("Vendas do mês", "Período: " + MONTH.format(mesAtual), vendas, "relatorio-vendas-mes.pdf");
    }

    private ResponseEntity<byte[]> vendasPdf(String title, String period, List<Pedido> vendas, String fileName)
            throws Exception {
        BigDecimal total = vendas.stream()
                .map(pedido -> pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<List<String>> rows = vendas.stream()
                .map(pedido -> List.of(
                        "#" + pedido.getId(),
                        "Mesa " + pedido.getMesa().getNumero(),
                        DATE_TIME.format(pedido.getCreatedAt()),
                        moeda(pedido.getTotal())))
                .toList();

        return pdf(
                title,
                period + "  |  Pedidos concluídos: " + vendas.size() + "  |  Total vendido: " + moeda(total),
                List.of("Pedido", "Mesa", "Data e hora", "Total"),
                rows,
                "Nenhuma venda concluída neste período.",
                new float[]{1.0f, 1.1f, 1.8f, 1.2f},
                List.of(3),
                fileName);
    }

    private ResponseEntity<byte[]> pdf(
            String title,
            String summary,
            List<String> headers,
            List<List<String>> rows,
            String emptyMessage,
            float[] columnWidths,
            List<Integer> rightAlignedColumns,
            String fileName) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 42, 42, 42, 54);
        PdfWriter writer = PdfWriter.getInstance(document, output);
        writer.setPageEvent(new ReportFooter());
        document.addTitle(title);
        document.addAuthor("Mesa & Sabor");
        document.addSubject("Relatório do sistema de gerenciamento de mesas");

        try {
            document.open();
            document.add(new Paragraph(title, new Font(Font.HELVETICA, 21, Font.BOLD, BRAND)));
            document.add(new Paragraph(summary, new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(75, 65, 65))));
            document.add(new Paragraph(
                    "Gerado em " + DATE_TIME.format(LocalDateTime.now()),
                    new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(120, 110, 110))));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);
            table.setWidths(columnWidths);
            table.setHeaderRows(1);
            table.setSplitRows(true);
            table.setSpacingBefore(8);

            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BRAND);
                cell.setBorderColor(BRAND);
                cell.setPadding(9);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }

            Font bodyFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(45, 38, 38));
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                List<String> row = rows.get(rowIndex);
                for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                    String value = columnIndex < row.size() ? row.get(columnIndex) : "";
                    PdfPCell cell = new PdfPCell(new Phrase(value, bodyFont));
                    cell.setBackgroundColor(rowIndex % 2 == 0 ? Color.WHITE : LIGHT_ROW);
                    cell.setBorderColor(BORDER);
                    cell.setPadding(8);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    if (rightAlignedColumns.contains(columnIndex)) {
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    table.addCell(cell);
                }
            }

            if (rows.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase(emptyMessage, bodyFont));
                emptyCell.setColspan(headers.size());
                emptyCell.setPadding(14);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setBorderColor(BORDER);
                table.addCell(emptyCell);
            }

            document.add(table);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(output.toByteArray());
    }

    private static String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(BRAZIL).format(valor == null ? BigDecimal.ZERO : valor);
    }

    private static String texto(String valor) {
        return valor == null || valor.isBlank() ? "—" : valor;
    }

    private static class ReportFooter extends PdfPageEventHelper {
        private final Font font = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(120, 110, 110));

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Phrase footer = new Phrase("Mesa & Sabor  •  Página " + writer.getPageNumber(), font);
            ColumnText.showTextAligned(
                    writer.getDirectContent(),
                    Element.ALIGN_CENTER,
                    footer,
                    (document.left() + document.right()) / 2,
                    document.bottom() - 24,
                    0);
        }
    }
}
