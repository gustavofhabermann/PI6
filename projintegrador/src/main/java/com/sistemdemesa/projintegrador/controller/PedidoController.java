package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.*;
import com.sistemdemesa.projintegrador.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/home/pedidos")
public class PedidoController {
    final PedidoRepository pedidos;
    final MesaRepository mesas;
    final ProdutoRepository produtos;

    public PedidoController(PedidoRepository p, MesaRepository m, ProdutoRepository pr) {
        pedidos = p;
        mesas = m;
        produtos = pr;
    }

    @GetMapping
    String listar(Model m) {
        m.addAttribute("pedidos", pedidos.findAll().stream().filter(p -> "aberto".equals(p.getStatus())).toList());
        return "app/pedidos/pedido";
    }

    @GetMapping("/pedido-produto/create")
    String novo(Model m) {
        m.addAttribute("mesas", mesas.findAll());
        m.addAttribute("produtos", produtos.findAll());
        return "app/pedidos/create";
    }

    @PostMapping("/pedido-produto")
    @Transactional
    String salvar(@RequestParam Long mesaId, @RequestParam List<Long> produtoId, @RequestParam List<Integer> quantidade,
            @RequestParam List<BigDecimal> precoUnitario) {
        if (produtoId.isEmpty())
            return "redirect:/home/pedidos/pedido-produto/create?erro=itens";
        Pedido p = new Pedido();
        p.setMesa(mesas.findById(mesaId).orElseThrow());
        p.setStatus("aberto");
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < produtoId.size(); i++) {
            Produto pr = produtos.findById(produtoId.get(i)).orElseThrow();
            PedidoProduto item = new PedidoProduto();
            item.setPedido(p);
            item.setProduto(pr);
            item.setQuantidade(quantidade.get(i));
            item.setPrecoUnitarioVendido(precoUnitario.get(i));
            item.setSubtotal(precoUnitario.get(i).multiply(BigDecimal.valueOf(quantidade.get(i))));
            p.getProdutosRelacao().add(item);
            total = total.add(item.getSubtotal());
        }
        p.setTotal(total);
        pedidos.save(p);
        return "redirect:/home/pedidos";
    }

    @PostMapping("/{id}/concluir")
    String concluir(@PathVariable Long id) {
        Pedido p = pedidos.findById(id).orElseThrow();
        p.setStatus("concluido");
        pedidos.save(p);
        return "redirect:/home/pedidos";
    }

    @PostMapping("/{id}/delete")
    String excluir(@PathVariable Long id) {
        pedidos.deleteById(id);
        return "redirect:/home/pedidos";
    }
}
