package ru.sberbank.jd.notesservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.UserRepository;

import java.util.Optional;

/**
 * Класс, определяющий существует ли пользователь.
 */
@Component
public class NotesServiceUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByName(username);

        return user.map(NotesServiceUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found" + username));
    }

}
