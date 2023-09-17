package ru.sberbank.jd.notesservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sberbank.jd.notesservice.dao.entity.Note;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.NoteRepository;
import ru.sberbank.jd.notesservice.dao.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс, содержащий бизнес логику для работы администратора.
 */
@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    NoteRepository noteRepository;

    /**
     * Метод блокировки пользователя.
     *
     * @param userId - id пользователя.
     */
    public void blockUser(UUID userId) {
        User user = userService.getUserById(userId);
        user.setBlocked(true);
        userRepository.save(user);
    }

    /**
     * Метод разблокировки пользователя.
     *
     * @param userId - id пользователя.
     */
    public void unblockUser(UUID userId) {
        User user = userService.getUserById(userId);
        user.setBlocked(false);
        userRepository.save(user);
    }

    /**
     * Метод, удаляющий пользователя и все его заметки.
     *
     * @param userId - id пользователя.
     */
    public void deleteUserById(UUID userId) {
        deleteAllUserNotes(userId);
        userRepository.deleteById(userId);
    }

    /**
     * Возвращает объект User по его ID.
     *
     * @param id UUID пользователя, которого надо достать из базы.
     * @return объект типа User.
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
     * Поиск пользователя и удаление всех его заметок.
     *
     * @param userId - id пользователя.
     */
    public void deleteAllUserNotes(UUID userId) {
        for (Note note : noteRepository.findAll()) {
            if (note.getOwner().getId().equals(userId)) {
                noteRepository.delete(note);
            }
        }
    }

    /**
     * Получение списка всех пользователей, не имеющих прав администратора.
     */
    public List<User> getAllUsersWithoutAdmins() {
        List<User> allUsersWithoutAdmins = userRepository.findAll().stream()
                .filter(user -> user.getAdminFlag().equals(false))
                .collect(Collectors.toList());

        return allUsersWithoutAdmins;
    }
}
