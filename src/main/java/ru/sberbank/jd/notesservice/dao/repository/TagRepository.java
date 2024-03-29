package ru.sberbank.jd.notesservice.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sberbank.jd.notesservice.dao.entity.Tag;

import java.util.UUID;

/**
 * Репозиторий для сущности Tag.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query(value = "SELECT tag_id, tag_value FROM T_TAG t WHERE lower(tag_value) = lower(:tagValue)",
            nativeQuery = true)
    Tag findByValueIgnoreCase(@Param("tagValue") String tagValue);


    @Query(value = "SELECT md5(:value)::uuid", nativeQuery = true)
    UUID getKey(@Param("value") String value);
}
