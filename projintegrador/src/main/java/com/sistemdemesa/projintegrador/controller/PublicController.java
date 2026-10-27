package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.User;
import com.sistemdemesa.projintegrador.repository.UserRepository;
import com.sistemdemesa.projintegrador.web.CadastroForm;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;

@Controller
public class PublicController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public PublicController(UserRepository u, PasswordEncoder e) {
        users = u;
        encoder = e;
    }

    @GetMapping("/")
    String index() {
        return "app/index/index";
    }

    @GetMapping("/login")
    String login() {
        return "pagLogin/login";
    }

    @GetMapping("/cadastro")
    String cadastro(Model model) {
        model.addAttribute("form", new CadastroForm());
        return "pagLogin/cadastrar";
    }

    @PostMapping("/cadastro")
    String criar(@Valid @ModelAttribute("form") CadastroForm form, BindingResult result) {
        String email = form.getEmail() == null ? "" : form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (!result.hasFieldErrors("email") && users.findByEmail(email).isPresent()) {
            result.rejectValue("email", "email.duplicado", "Este e-mail já está cadastrado.");
        }
        if (result.hasErrors()) {
            form.setPassword(null);
            return "pagLogin/cadastrar";
        }

        User user = new User();
        user.setName(form.getName().trim());
        user.setEmail(email);
        user.setPassword(encoder.encode(form.getPassword()));
        users.save(user);
        return "redirect:/login?cadastrado=true";
    }

}
