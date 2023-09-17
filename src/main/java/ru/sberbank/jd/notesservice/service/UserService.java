package ru.sberbank.jd.notesservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Класс содержит методы взаимодействия с БД для таблицы T_USER.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Возвращает объект User по его ID.
     *
     * @param id UUID пользователя, которого надо достать из базы
     * @return объект типа User. ??? что вернет, когда нет объекта не понятно
     */
    public User getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }


    /**
     * Возвращает объект User по ID в виде строки.
     *
     * @param id идентификатор пользователя
     * @return если передан некорректный UUID, то вернет null (через exception)
     */
    public User getUserById(String id) {
        User user;
        try {
            UUID uuid  =  UUID.fromString(id);
            user = getUserById(uuid);
        } catch (IllegalArgumentException e) {
            user = null;
        }

        return user;
    }


    /**
     * Возвращает авторизованного пользователя.
     *
     * @return объект User под которым создаются заметки, либо null, если пользователь не авторизован.
     */
    public User getCurrentUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser")) {
            return null;
        }
        NotesServiceUserDetails userDetails = (NotesServiceUserDetails) principal;

        Optional<User> user = userRepository.findByName(userDetails.getUsername());
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }

    /**
     * Добавляет пользователя.
     *
     * @param user объект тип User
     * @return true если пользователь существует, false в противном случае
     */
    public boolean addUser(User user) {
        boolean isError = false;
        Optional<User> existsUser = userRepository.findByName(user.getName());
        System.out.println(user.getName());
        if (existsUser.isPresent()) {
            isError = true;
        } else {
            user.setId(UUID.randomUUID());
            user.setName(user.getName());
            user.setPassword(encoder.encode(user.getPassword()));
            user.setLogin(user.getLogin());
            user.setTelegramLogin(user.getTelegramLogin());
            user.setAdminFlag(false);
            user.setBlocked(false);
            userRepository.save(user);
        }
        return isError;
    }

    /**
     * Метод, возвращающий список всех публичных записей БД T_USER.
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
