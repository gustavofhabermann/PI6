package com.sistemdemesa.projintegrador.config;

import com.sistemdemesa.projintegrador.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Locale;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByEmail(username.trim().toLowerCase(Locale.ROOT))
                .map(u -> User.withUsername(u.getEmail()).password(u.getPassword())
                        .roles("USER").build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        a -> a.requestMatchers("/", "/login", "/cadastro", "/css/**", "/js/**", "/img/**", "/error")
                                .permitAll().anyRequest().authenticated())
                .formLogin(f -> f.loginPage("/login").loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home/mesas", true).failureUrl("/login?erro=true").permitAll())
                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true").permitAll());
        return http.build();
    }
}
