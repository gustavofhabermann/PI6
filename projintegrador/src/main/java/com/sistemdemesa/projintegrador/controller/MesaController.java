package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.Mesa;
import com.sistemdemesa.projintegrador.repository.MesaRepository;
import com.sistemdemesa.projintegrador.repository.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/mesas")
public class MesaController {
    private final MesaRepository repo;
    private final PedidoRepository pedidos;

    public MesaController(MesaRepository r, PedidoRepository pedidos) {
        repo = r;
        this.pedidos = pedidos;
    }

    @InitBinder
    void bloquearId(WebDataBinder binder) {
        binder.setDisallowedFields("id");
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
    String salvar(@Valid @ModelAttribute("mesa") Mesa mesa, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/home/mesas/create?erro=dados";
        }
        if (repo.existsByNumero(mesa.getNumero())) {
            return "redirect:/home/mesas/create?erro=numero";
        }
        repo.save(mesa);
        return "redirect:/home/mesas";
    }

    @GetMapping("/{id}/update")
    String editar(@PathVariable Long id, Model m) {
        m.addAttribute("mesa", repo.findById(id).orElseThrow());
        return "app/home/editar";
    }

    @PostMapping("/{id}/update")
    String atualizar(@PathVariable Long id, @Valid @ModelAttribute("mesa") Mesa mesa, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/home/mesas/" + id + "/update?erro=dados";
        }
        if (repo.existsByNumeroAndIdNot(mesa.getNumero(), id)) {
            return "redirect:/home/mesas/" + id + "/update?erro=numero";
        }
        Mesa e = repo.findById(id).orElseThrow();
        e.setNumero(mesa.getNumero());
        e.setQuantidade(mesa.getQuantidade());
        repo.save(e);
        return "redirect:/home/mesas";
    }

    @PostMapping("/{id}/delete")
    String excluir(@PathVariable Long id) {
        if (pedidos.existsByMesa_Id(id)) {
            return "redirect:/home/mesas?erro=em-uso";
        }
        repo.deleteById(id);
        return "redirect:/home/mesas";
    }
}
