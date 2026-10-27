package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.*;
import com.sistemdemesa.projintegrador.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/home/pedidos")
public class PedidoController {
    private final PedidoRepository pedidos;
    private final MesaRepository mesas;
    private final ProdutoRepository produtos;

    public PedidoController(PedidoRepository p, MesaRepository m, ProdutoRepository pr) {
        pedidos = p;
        mesas = m;
        produtos = pr;
    }

    @GetMapping
    String listar(Model m) {
        m.addAttribute("pedidos", pedidos.findByStatus("aberto"));
        return "app/pedidos/pedido";
    }

    @GetMapping("/pedido-produto/create")
    String novo(Model m) {
        m.addAttribute("mesas", mesas.findAll());
        m.addAttribute("produtos", produtos.findAll().stream()
                .filter(produto -> produto.getQuantidade() != null && produto.getQuantidade() > 0)
                .toList());
        return "app/pedidos/create";
    }

    @PostMapping("/pedido-produto")
    @Transactional
    String salvar(@RequestParam(required = false) Long mesaId,
            @RequestParam(required = false) List<Long> produtoId,
            @RequestParam(required = false) List<Integer> quantidade) {
        if (mesaId == null || produtoId == null || quantidade == null || produtoId.isEmpty()
                || produtoId.size() != quantidade.size()) {
            return "redirect:/home/pedidos/pedido-produto/create?erro=dados";
        }

        var mesa = mesas.findById(mesaId).orElse(null);
        if (mesa == null) {
            return "redirect:/home/pedidos/pedido-produto/create?erro=mesa";
        }

        Map<Long, Integer> quantidadePorProduto = new LinkedHashMap<>();
        for (int i = 0; i < produtoId.size(); i++) {
            Long id = produtoId.get(i);
            Integer qtd = quantidade.get(i);
            if (id == null || qtd == null || qtd < 1) {
                return "redirect:/home/pedidos/pedido-produto/create?erro=dados";
            }
            int jaSolicitado = quantidadePorProduto.getOrDefault(id, 0);
            if (qtd > Integer.MAX_VALUE - jaSolicitado) {
                return "redirect:/home/pedidos/pedido-produto/create?erro=dados";
            }
            quantidadePorProduto.merge(id, qtd, (atual, adicional) -> Math.addExact(atual, adicional));
        }

        Map<Long, Produto> produtosPorId = new HashMap<>();
        produtos.findAllByIdForUpdate(quantidadePorProduto.keySet())
                .forEach(produto -> produtosPorId.put(produto.getId(), produto));
        if (produtosPorId.size() != quantidadePorProduto.size()) {
            return "redirect:/home/pedidos/pedido-produto/create?erro=produto";
        }
        for (Map.Entry<Long, Integer> entry : quantidadePorProduto.entrySet()) {
            Produto produto = produtosPorId.get(entry.getKey());
            if (produto.getQuantidade() == null || produto.getQuantidade() < entry.getValue()) {
                return "redirect:/home/pedidos/pedido-produto/create?erro=estoque";
            }
            if (produto.getPrecoVenda() == null || produto.getPrecoVenda().signum() < 0) {
                return "redirect:/home/pedidos/pedido-produto/create?erro=produto";
            }
        }

        Pedido p = new Pedido();
        p.setMesa(mesa);
        p.setStatus("aberto");
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < produtoId.size(); i++) {
            Produto pr = produtosPorId.get(produtoId.get(i));
            PedidoProduto item = new PedidoProduto();
            item.setPedido(p);
            item.setProduto(pr);
            item.setQuantidade(quantidade.get(i));
            item.setPrecoUnitarioVendido(pr.getPrecoVenda());
            item.setSubtotal(pr.getPrecoVenda().multiply(BigDecimal.valueOf(quantidade.get(i))));
            p.getProdutosRelacao().add(item);
            total = total.add(item.getSubtotal());
        }
        quantidadePorProduto.forEach((id, qtd) -> {
            Produto produto = produtosPorId.get(id);
            produto.setQuantidade(produto.getQuantidade() - qtd);
        });
        produtos.saveAll(produtosPorId.values());
        p.setTotal(total);
        pedidos.save(p);
        return "redirect:/home/pedidos";
    }

    @PostMapping("/{id}/concluir")
    @Transactional
    String concluir(@PathVariable Long id) {
        Pedido p = pedidos.findById(id).orElseThrow();
        if ("aberto".equals(p.getStatus())) {
            p.setStatus("concluido");
        }
        return "redirect:/home/pedidos";
    }

    @PostMapping("/{id}/delete")
    @Transactional
    String excluir(@PathVariable Long id) {
        Pedido pedido = pedidos.findById(id).orElse(null);
        if (pedido == null) {
            return "redirect:/home/pedidos";
        }
        if (!"aberto".equals(pedido.getStatus())) {
            return "redirect:/home/pedidos?erro=pedido-concluido";
        }

        pedido.getProdutosRelacao().forEach(item -> {
            Produto produto = item.getProduto();
            int estoqueAtual = produto.getQuantidade() == null ? 0 : produto.getQuantidade();
            produto.setQuantidade(Math.addExact(estoqueAtual, item.getQuantidade()));
        });
        pedidos.delete(pedido);
        return "redirect:/home/pedidos";
    }
}
