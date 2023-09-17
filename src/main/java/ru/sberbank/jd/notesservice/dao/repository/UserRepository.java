package ru.sberbank.jd.notesservice.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.jd.notesservice.dao.entity.Tag;
import ru.sberbank.jd.notesservice.dao.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA репозиторий для сущности User.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByName(String name);

    Optional<User> findByTelegramLogin(String telegramLogin);
}
