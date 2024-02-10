package ru.sberbank.jd.notesservice.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sberbank.jd.notesservice.dao.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA репозиторий для сущности User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByName(String name);

    Optional<User> findByTelegramLogin(String telegramLogin);

    @Query(value = "select * from t_user where user_name like concat('%',:name,'%')", nativeQuery = true)
    List<User> findUsersByName(@Param("name")String name);
}
