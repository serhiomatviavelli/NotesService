package ru.sberbank.jd.notesservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sberbank.jd.notesservice.dao.entity.Note;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.NoteRepository;
import ru.sberbank.jd.notesservice.dao.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс, содержащий бизнес логику для работы администратора.
 */
@AllArgsConstructor
@Service
public class AdminService {

    private UserRepository userRepository;

    private NoteRepository noteRepository;

    /**
     * Метод блокировки пользователя.
     *
     * @param userId - id пользователя.
     */
    public void blockUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBlocked(true);
            userRepository.save(user);
        }
    }

    /**
     * Метод разблокировки пользователя.
     *
     * @param userId - id пользователя.
     */
    public void unblockUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBlocked(false);
            userRepository.save(user);
        }
    }

    /**
     * Метод, удаляющий пользователя и все его заметки.
     *
     * @param userId - id пользователя.
     */
    public void deleteUserById(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            deleteAllUserNotes(userId);
            userRepository.deleteById(userId);
        }
    }

    /**
     * Поиск пользователя и удаление всех его заметок.
     *
     * @param userId - id пользователя.
     */
    public void deleteAllUserNotes(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            List<Note> userNotes = noteRepository.findByOwner(userOptional.get());
            noteRepository.deleteAll(userNotes);
        }
    }

    /**
     * Получение списка всех пользователей, не имеющих прав администратора.
     */
    public List<User> getAllUsersWithoutAdmins(String name) {
        if (name == null) {
            return userRepository.findAll().stream()
                    .filter(user -> user.getAdminFlag().equals(false))
                    .collect(Collectors.toList());
        } else {
            return userRepository.findUsersByName(name).stream()
                    .filter(user -> user.getAdminFlag().equals(false))
                    .collect(Collectors.toList());
        }
    }
}
