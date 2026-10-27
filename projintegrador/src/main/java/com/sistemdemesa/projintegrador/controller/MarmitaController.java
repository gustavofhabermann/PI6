package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.Marmita;
import com.sistemdemesa.projintegrador.repository.MarmitaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/marmitas")
public class MarmitaController {
    private final MarmitaRepository repo;

    public MarmitaController(MarmitaRepository r) {
        repo = r;
    }

    @InitBinder
    void bloquearId(WebDataBinder binder) {
        binder.setDisallowedFields("id");
    }

    @GetMapping
    String listar(Model m) {
        m.addAttribute("marmitas", repo.findAll());
        return "app/marmitas/marmitas";
    }

    @GetMapping("/create")
    String criar(Model m) {
        m.addAttribute("marmita", new Marmita());
        return "app/marmitas/marmitas_create";
    }

    @PostMapping("/create")
    String salvar(@Valid @ModelAttribute("marmita") Marmita x, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/home/marmitas/create?erro=dados";
        }
        repo.save(x);
        return "redirect:/home/marmitas";
    }

    @GetMapping("/{id}/update")
    String editar(@PathVariable Long id, Model m) {
        m.addAttribute("marmita", repo.findById(id).orElseThrow());
        return "app/marmitas/marmitas_update";
    }

    @PostMapping("/{id}/update")
    String atualizar(@PathVariable Long id, @Valid @ModelAttribute("marmita") Marmita x, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/home/marmitas/" + id + "/update?erro=dados";
        }
        Marmita e = repo.findById(id).orElseThrow();
        e.setNomeCliente(x.getNomeCliente());
        e.setTelefone(x.getTelefone());
        e.setEndereco(x.getEndereco());
        e.setTipo(x.getTipo());
        e.setPreco(x.getPreco());
        e.setStatus(x.getStatus());
        repo.save(e);
        return "redirect:/home/marmitas";
    }

    @PostMapping("/{id}/delete")
    String excluir(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/home/marmitas";
    }
}
