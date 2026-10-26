package com.sistemdemesa.projintegrador.controller;

import com.sistemdemesa.projintegrador.entity.User;
import com.sistemdemesa.projintegrador.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    String cadastro() {
        return "pagLogin/cadastrar";
    }

    @PostMapping("/cadastro")
    String criar(User user) {
        if (users.findByEmail(user.getEmail()).isPresent())
            return "redirect:/cadastro?erro=email";
        user.setPassword(encoder.encode(user.getPassword()));
        users.save(user);
        return "redirect:/login?cadastrado=true";
    }

}