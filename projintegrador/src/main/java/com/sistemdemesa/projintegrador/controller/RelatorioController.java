package com.sistemdemesa.projintegrador.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sistemdemesa.projintegrador.entity.Pedido;
import com.sistemdemesa.projintegrador.repository.PedidoRepository;
import com.sistemdemesa.projintegrador.repository.ProdutoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Controller
@RequestMapping("/home/relatorios")
public class RelatorioController {
    private final ProdutoRepository produtos;
    private final PedidoRepository pedidos;

    public RelatorioController(ProdutoRepository p, PedidoRepository pd) {
        produtos = p;
        pedidos = pd;
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
        return pdf("Relatório de Produtos",
                produtos.findAll().stream().map(p -> p.getNome() + " - R$ " + p.getPrecoVenda()).toList());
    }

    @GetMapping("/exportar/vendasdia")
    ResponseEntity<byte[]> dia() throws Exception {
        LocalDate d = LocalDate.now();
        return vendasPdf("Vendas do dia", pedidos.findAll().stream().filter(p -> "concluido".equals(p.getStatus())
                && p.getCreatedAt() != null && p.getCreatedAt().toLocalDate().equals(d)).toList());
    }

    @GetMapping("/exportar/vendasmes")
    ResponseEntity<byte[]> mes() throws Exception {
        YearMonth ym = YearMonth.now();
        return vendasPdf("Vendas do mês", pedidos.findAll().stream().filter(p -> "concluido".equals(p.getStatus())
                && p.getCreatedAt() != null && YearMonth.from(p.getCreatedAt()).equals(ym)).toList());
    }

    private ResponseEntity<byte[]> vendasPdf(String title, List<Pedido> ps) throws Exception {
        return pdf(title,
                ps.stream().map(
                        p -> "Pedido #" + p.getId() + " | Mesa " + p.getMesa().getNumero() + " | R$ " + p.getTotal())
                        .toList());
    }

    private ResponseEntity<byte[]> pdf(String title, List<String> lines) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document d = new Document();
        PdfWriter.getInstance(d, out);
        d.open();
        d.add(new Paragraph(title));
        d.add(new Paragraph(" "));
        for (String s : lines)
            d.add(new Paragraph(s));
        d.close();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio.pdf")
                .contentType(MediaType.APPLICATION_PDF).body(out.toByteArray());
    }
}
