package ru.sberbank.jd.notesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.sberbank.jd.notesservice.service.NotesServiceUserDetailsService;

/**
 * Конфигурация Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new NotesServiceUserDetailsService();
    }

    /**
     * Распределение адресов, которые могут посещать авторизованные и неавторизованные пользователи.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/",
                        "/css/**",
                        "/webjars/**",
                        "/note/all",
                        "/note/userNotes",
                        "/note/view",
                        "/auth/**")
                .permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers("/note/create", "/note/delete",
                         "/note/edit", "/note/save", "/admin/**")
                .authenticated().and().formLogin().defaultSuccessUrl("/note/all", true)
                .and().build();
    }

    /**
     * Кодировщик паролей.
     */
    @Bean
    public PasswordEncoder passwordEncoder()  {
        return new BCryptPasswordEncoder();
    }

    /**
     * Поставщик аутентификации.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
