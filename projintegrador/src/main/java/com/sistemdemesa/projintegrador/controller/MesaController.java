package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.Mesa;
import com.sistemdemesa.projintegrador.repository.MesaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/mesas")
public class MesaController {
    private final MesaRepository repo;

    public MesaController(MesaRepository r) {
        repo = r;
    }

    @GetMapping
    String listar(Model m) {
        m.addAttribute("mesas", repo.findAll());
        return "app/home/home";
    }

    @GetMapping("/create")
    String criar(Model m) {
        m.addAttribute("mesa", new Mesa());
        return "app/home/cadastro";
    }

    @PostMapping("/create")
    String salvar(@Valid @ModelAttribute Mesa mesa) {
        repo.save(mesa);
        return "redirect:/home/mesas";
    }

    @GetMapping("/{id}/update")
    String editar(@PathVariable Long id, Model m) {
        m.addAttribute("mesa", repo.findById(id).orElseThrow());
        return "app/home/editar";
    }

    @PostMapping("/{id}/update")
    String atualizar(@PathVariable Long id, @Valid @ModelAttribute Mesa mesa) {
        mesa.getClass();
        Mesa e = repo.findById(id).orElseThrow();
        e.setNumero(mesa.getNumero());
        e.setQuantidade(mesa.getQuantidade());
        repo.save(e);
        return "redirect:/home/mesas";
    }

    @PostMapping("/{id}/delete")
    String excluir(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/home/mesas";
    }
}
