package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.Produto;
import com.sistemdemesa.projintegrador.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/produtos")
public class ProdutoController {
    private final ProdutoRepository repo;

    public ProdutoController(ProdutoRepository r) {
        repo = r;
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
    String salvar(@Valid @ModelAttribute Produto p) {
        repo.save(p);
        return "redirect:/home/produtos";
    }

    @GetMapping("/{id}/update")
    String editar(@PathVariable Long id, Model m) {
        m.addAttribute("produto", repo.findById(id).orElseThrow());
        return "app/produtos/produtos_update";
    }

    @PostMapping("/{id}/update")
    String atualizar(@PathVariable Long id, @Valid @ModelAttribute Produto p) {
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
        repo.deleteById(id);
        return "redirect:/home/produtos";
    }
}
