package ru.sberbank.jd.notesservice.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.sberbank.jd.notesservice.dao.entity.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, предоставляющий информацию о зарегистрированном пользователе.
 */
public class NotesServiceUserDetails implements UserDetails {

    private String name;
    private String password;
    private List<GrantedAuthority> authorities;
    private boolean blocked;

    /**
     * Конструктор, определяющий имя пользователя, пароль и роли.
     */
    public NotesServiceUserDetails(User user) {
        name = user.getName();
        password = user.getPassword();
        blocked = user.isBlocked();
        String roles = "";

        if (user.getAdminFlag().equals(false)) {
            roles += "ROLE_USER";
        } else {
            roles += "ROLE_ADMIN";
        }
        authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.blocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
