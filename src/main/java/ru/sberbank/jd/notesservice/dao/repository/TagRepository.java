package ru.sberbank.jd.notesservice.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.jd.notesservice.dao.entity.Tag;

import java.util.UUID;

/**
 * Репозиторий для сущности Tag.
 */
public interface TagRepository extends JpaRepository<Tag, UUID> {

    //TODO: нужно сделать отбор меток по пользователю

    @Query(value = "SELECT tag_id, tag_value FROM T_TAG t WHERE lower(tag_value) = lower(:tagValue)",
            nativeQuery = true)
    Tag findByValueIgnoreCase(@Param("tagValue") String tagValue);


    @Query(value = "SELECT md5(:value)::uuid", nativeQuery = true)
    UUID getKey(@Param("value") String value);
}
