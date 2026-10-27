package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.Produto;
import com.sistemdemesa.projintegrador.repository.PedidoProdutoRepository;
import com.sistemdemesa.projintegrador.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/produtos")
public class ProdutoController {
    private final ProdutoRepository repo;
    private final PedidoProdutoRepository itensPedido;

    public ProdutoController(ProdutoRepository r, PedidoProdutoRepository itensPedido) {
        repo = r;
        this.itensPedido = itensPedido;
    }

    @InitBinder
    void bloquearId(WebDataBinder binder) {
        binder.setDisallowedFields("id");
    }

    @GetMapping
    String listar(Model m) {
        m.addAttribute("produtos", repo.findAll());
        return "app/produtos/produtos";
    }

    @GetMapping("/create")
    String criar(Model m) {
        m.addAttribute("produto", new Produto());
        return "app/produtos/produtos_create";
    }

    @PostMapping("/create")
    String salvar(@Valid @ModelAttribute("produto") Produto p, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/home/produtos/create?erro=dados";
        }
        repo.save(p);
        return "redirect:/home/produtos";
    }

    @GetMapping("/{id}/update")
    String editar(@PathVariable Long id, Model m) {
        m.addAttribute("produto", repo.findById(id).orElseThrow());
        return "app/produtos/produtos_update";
    }

    @PostMapping("/{id}/update")
    String atualizar(@PathVariable Long id, @Valid @ModelAttribute("produto") Produto p, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/home/produtos/" + id + "/update?erro=dados";
        }
        Produto e = repo.findById(id).orElseThrow();
        e.setNome(p.getNome());
        e.setCategoria(p.getCategoria());
        e.setPrecoCompra(p.getPrecoCompra());
        e.setPrecoVenda(p.getPrecoVenda());
        e.setQuantidade(p.getQuantidade());
        repo.save(e);
        return "redirect:/home/produtos";
    }

    @PostMapping("/{id}/delete")
    String excluir(@PathVariable Long id) {
        if (itensPedido.existsByProduto_Id(id)) {
            return "redirect:/home/produtos?erro=em-uso";
        }
        repo.deleteById(id);
        return "redirect:/home/produtos";
    }
}
