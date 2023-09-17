package ru.sberbank.jd.notesservice.dao.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.jd.notesservice.dao.entity.Note;
import ru.sberbank.jd.notesservice.dao.entity.Tag;
import ru.sberbank.jd.notesservice.dao.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий, позволяющий взаимодействовать с сущностями БД T_NOTE.
 */
public interface NoteRepository extends JpaRepository<Note, UUID> {

    // возвращаем все заметки пользователя (меню My Notes)
    // note/userNotes?id=<ID пользователя>
    List<Note> findByOwner(User user);

    // возвращаем публичные заметки пользователя (меню My Notes)
    // note/userNotes?id=<ID чужного пользователя>
    List<Note> findByOwnerAndPublicFlag(User user, Boolean publicFlag);

    // ищем все заметки по флагу публичности (все публичные заметки)
    // note/all + пользователя нет
    List<Note> findByPublicFlag(Boolean publicFlag);

    List<Note> findByTitle(String title);

    // ищем все публичные заметки
    // note/all + пользователя нет
    @Query(value = "SELECT U.* FROM t_note U WHERE public_flag = true", nativeQuery = true)
    List<Note> findNotes();

    //ищем все публичные заметки с фильтром по тэгу
    // note/all + таг
    @Query(value =
            "SELECT N.* "
                    + "FROM t_note N inner join t_note_tag T on N.note_id = T.note_id "
                    + "WHERE 1 = 1 "
                    + "  and public_flag =true "
                    + "  and T.tag_id = :tagId",
            nativeQuery = true)
    List<Note> findNotesByTag(@Param("tagId") UUID tagId);


    // ищем все заметки пользователя + публичные заметки
    // note/all + пользователь есть
    @Query(value = "SELECT U.* FROM t_note U WHERE public_flag = true OR owner_id = :userId", nativeQuery = true)
    List<Note> findAllNotesByOwner(@Param("userId") UUID userId);


    //ищем все заметки пользователя + публичные с фильтром по тэгу
    // note/all + пользователь + таг
    @Query(value =
            "SELECT N.* "
                    + "FROM t_note N inner join t_note_tag T on N.note_id = T.note_id "
                    + "WHERE 1 = 1 "
                    + "  and (owner_id = :userId or public_flag =true) "
                    + "  and T.tag_id = :tagId",
            nativeQuery = true)
    List<Note> findAllNotesByOwnerAndTag(@Param("userId") UUID userId, @Param("tagId") UUID tagId);

    @Query(value = "SELECT * FROM T_NOTE LIMIT 5 OFFSET :offset", nativeQuery = true)
    List<Note> getPagedNotes(@Param("offset") int offset);
}
