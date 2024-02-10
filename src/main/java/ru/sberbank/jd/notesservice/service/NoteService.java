package ru.sberbank.jd.notesservice.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.sberbank.jd.notesservice.dao.entity.Note;
import ru.sberbank.jd.notesservice.dao.entity.Tag;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.NoteRepository;
import ru.sberbank.jd.notesservice.dto.NoteDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс, содержащий методы взаимодействия с базой данных для таблицы T_NOTE.
 */
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    private final TagService tagService;

    public final UserService userService;

    private final ModelMapper modelMapper;



    /**
     * Метод, возвращающий список всех записей БД T_NOTE.
     */
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }


    /**
     * Метод, возвращающий список всех публичных записей БД T_NOTE.
     */
    public List<Note> getNotes() {
        return noteRepository.findNotes();
    }

    /**
     * Метод, возвращающий список всех публичных записей БД T_NOTE.
     */
    public List<Note> getNotesByTag(UUID tagId) {
        return noteRepository.findNotesByTag(tagId);
    }

    /**
     * Метод, возвращающий список всех записей пользователя.
     */
    public List<Note> getAllUserNotes(UUID userId) {
        return noteRepository.findByOwner(userService.getUserById(userId));
    }

    /**
     * Возвращает список заметок. Состав списка: все заметки пользователя + публичные заметки.
     *
     * @param userId идентификатор пользователя в формате UUID
     * @return список заметок пользователя или публичных заметок из БД
     */
    public List<Note> getAllNotesByOwner(UUID userId) {
        return noteRepository.findAllNotesByOwner(userId);
    }


    /**
     * Возвращает список заметок. Состав списка: (все заметки пользователя + публичные заметки) + фильтр по метке.
     *
     * @param userId идентификатор пользователя в формате UUID
     * @param tagId идентификатор метки
     * @return список заметок пользователя или публичных заметок из БД + фильтр по метке
     */
    public List<Note> getAllNotesByOwnerAndTag(UUID userId, UUID tagId) {
        return noteRepository.findAllNotesByOwnerAndTag(userId, tagId);
    }

    /**
     * Возвращает список заметок пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список заметок
     */
    public List<Note> getUserNotes(String userId) {

        // получаем текущего пользователя
        User activeUser = userService.getCurrentUser();

        // ищем пользователя в БД
        User user = userService.getUserById(userId);

        if (user == null) {
            // пользователь не найдет (передан отсутствующий в БД ID) -> возвращаем пустой список
            return new ArrayList<>();
        } else if (activeUser != null && activeUser.getId() == user.getId()) {
            // пользователь найден и совпадает с текущим пользователем -> возвращаем все заметки пользователя
            return noteRepository.findByOwner(user);
        } else {
            // нет текущего пользователя или он не равен пользователю переданному в качестве параметра
            // -> возвращаем только публичные заметки пользователя
            return noteRepository.findByOwnerAndPublicFlag(user, true);
        }
    }


    /**
     * Создает объект Note и сохраняет его в базу данных.
     *
     * @param noteDto - объект, на основании которого создает Note. Содержит необходимые поля из формы.
     * @param owner - пользователь, который создает объект.
     * @return созданный объект Note
     */
    public Note saveNote(NoteDto noteDto, User owner) {
        Note note = new Note();

        // ID заметки
        if (noteDto.getId() != null) {
            note.setId(noteDto.getId());
        }

        // заголовок заметки
        note.setTitle(noteDto.getTitle());

        // текст заметки
        note.setText(noteDto.getText());

        // владелец заметки
        if (noteDto.getOwner() != null) {
            note.setOwner(noteDto.getOwner());
        } else {
            note.setOwner(owner);
        }

        // признак публичной заметки
        if (noteDto.getPublicFlag() != null) {
            note.setPublicFlag(noteDto.getPublicFlag());
        } else {
            note.setPublicFlag(false);
        }

        // дата создания заметки
        if (noteDto.getCreateDateTime() != null) {
            note.setCreateDateTime(noteDto.getCreateDateTime());
        } else {
            note.setCreateDateTime(LocalDateTime.now());
        }

        // список меток
        String str = noteDto.getTagString();
        Set tagSet = getTagSet(str);
        note.setTags(tagSet);

        // дата редактирования заметки
        note.setEditDateTime(LocalDateTime.now());

        noteRepository.save(note);
        noteRepository.flush();
        return note;
    }

    /**
     * Метод, удаляющий элемент БД T_NOTE по его id.
     */
    public void deleteNoteById(UUID id) {
        noteRepository.deleteById(id);
    }

    /**
     * Ищем заметку по переданному ID.
     *
     * @param id - идентификатор заметки для просмотра в виде UUID
     * @return объект Note, если нашли, null в противном случае
     */
    public Note getNoteById(UUID id) {
        Optional<Note> optionalNote = noteRepository.findById(id);
        return optionalNote.orElse(null);
    }

    /**
     * Ищем заметку по переданному ID.
     *
     * @param id - идентификатор заметки для просмотра в виде строки
     * @return объект Note, если нашли, null в противном случае
     */
    public Note getNoteById(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return getNoteById(uuid);
    }


    /**
     * Конвертация сущности Note в NoteDto.
     */
    public NoteDto convertToDto(Note note) {
        NoteDto noteDto = modelMapper.map(note, NoteDto.class);
        noteDto.setFormattedText(convertToMarkDown(note.getText()));
        noteDto.setTagString(setToString(note.getTags()));
        return noteDto;
    }

    /**
     * Конвертация текста, написанного синтаксисом MD в html код.
     */
    private String convertToMarkDown(String text) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(text);

        return renderer.render(document);
    }


    /**
     * Конвертация списка тегов в строку.
     */
    private String setToString(Set<Tag> tags) {
        Set<String> s = tags.stream().map(Tag::getValue).collect(Collectors.toSet());
        return String.join(" ", s);
    }

    /**
     * Конвертация строки в список тегов.
     */
    private Set<Tag> getTagSet(String tagString) {
        Set<Tag> tagSet = new HashSet<>();

        Tag tag;
        Set<String> strings = Arrays.stream(tagString.split(" "))
                .filter(t -> t.length() > 0)
                .collect(Collectors.toSet());

        for (String str : strings) {
            tag = tagService.getTagByValue(str);
            if (tag == null) {
                tag = tagService.addTag(str);
            }
            tagSet.add(tag);
        }

        return tagSet;
    }

    /**
     * Возвращает имя автора по заголовку заметки (для Telegram-бота).
     *
     * @param title - заголовок заметки.
     * @return - строковое имя автора.
     */
    public String getAuthor(String title) {
        List<Note> note = noteRepository.findByTitle(title);
        return note.get(0).getOwner().getLogin();
    }

    /**
     * Возвращает пять случайных публичных заметок (для Telegram-бота).
     *
     * @return - все публичные заметки, если их не больше пяти, иначе - пять случайных.
     */
    public List<Note> getFiveRandomNotes() {
        List<Note> notes = new ArrayList<>(noteRepository.findNotes());
        Collections.shuffle(notes);
        List<Note> fiveRandomNotes = new ArrayList<>();
        if (notes.size() > 5) {
            for (int i = 0; i < 5; i++) {
                fiveRandomNotes.add(notes.get(i));
            }
        } else {
            fiveRandomNotes.addAll(notes);
        }

        return fiveRandomNotes;
    }
}
