package ru.sberbank.jd.notesservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sberbank.jd.notesservice.dao.entity.Note;
import ru.sberbank.jd.notesservice.dao.entity.Tag;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dto.NoteDto;
import ru.sberbank.jd.notesservice.service.NoteService;
import ru.sberbank.jd.notesservice.service.TagService;
import ru.sberbank.jd.notesservice.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Контроллер UI.
 */
@AllArgsConstructor
@Controller
@RequestMapping(value = "/note")
public class NoteController {

    private NoteService noteService;

    private UserService userService;

    private TagService tagService;

    /**
     * Обрабатываем запрос на выдачу всех заметок.
     *
     * @param model - объект для передачи значений клиенту (Thymeleaf).
     * @return возвращает адрес (относительный путь), куда надо идти клиенту.
     */
    @GetMapping("/all")
    public String all(Model model,
                      @RequestParam(value = "id", required = false) String tagId) {
        User user = applicationUser();

        if (tagId == null) {
            if (user == null) {
                model.addAttribute("notes", noteService.getNotes());
            } else if (user.getAdminFlag().equals(true)) {
                model.addAttribute("notes", noteService.getAllNotes());
            } else {
                model.addAttribute("notes", noteService.getAllNotesByOwner(user.getId()));
            }
        } else {
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return "redirect:note/all";
            } else {
                model.addAttribute("tagName", tag.getValue());
                if (user == null) {
                    model.addAttribute("notes", noteService.getNotesByTag(tag.getId()));
                } else if (user.getAdminFlag().equals(true)) {
                    model.addAttribute("notes", noteService.getAllNotes());
                } else {
                    model.addAttribute("notes", noteService.getAllNotesByOwnerAndTag(user.getId(), tag.getId()));
                }
            }
        }

        return "all";
    }

    /**
     * Возвращает все заметки данного пользователя.
     *
     * @param model модель для передачи данных Thymeleaf.
     * @param id идентификатор пользователя, владельца заметки.
     * @return шаблон для отображения
     */
    @GetMapping("/userNotes")
    public String viewUserNotes(Model model, @RequestParam(name = "id", required = false,
            defaultValue = "null") String id) {
        User user = applicationUser();

        if (id == null) {
            return "redirect:/note/all";
        } else if (user != null && user.getAdminFlag().equals(true)) {
            model.addAttribute("notes", noteService.getAllUserNotes(UUID.fromString(id)));
            return "all";
        } else {
            model.addAttribute("notes", noteService.getUserNotes(id));
            return "all";
        }

    }

    /**
     * Обработка запроса на просмотр заметки.
     *
     * @param model - объект дя передачи параметров Thymeleaf
     * @param id - идентификатор заметки, которую надо отобразить в окне просмотра
     * @return возвращает адрес (относительный путь), куда надо идти клиенту.
     */
    @GetMapping("/view")
    public String view(Model model, @RequestParam("id") String id) {

        NoteDto noteDto = noteService.convertToDto(noteService.getNoteById(UUID.fromString(id)));

        model.addAttribute("noteDto", noteDto);
        return "view";
    }


    /**
     * Обработка запроса на создание новой заявки.
     * В результате должна открыться форма редактирования заявки.
     *
     * @param model - объект для передачи значений клиенту (Thymeleaf).
     * @return возвращает адрес (относительный путь), куда надо идти клиенту.
     */
    @GetMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String create(Model model) {
        NoteDto noteDto = new NoteDto();
        model.addAttribute("noteDto", noteDto);
        return "edit";
    }


    /**
     * Метод отвечает за подготовку данных для формы редактирования заметки.
     *
     * @param id - идентификатор заметки
     * @param model - модель
     * @return шаблон страницы, которая отвечает за редактирование
     */
    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String edit(@RequestParam("id") String id, Model model) {

        try {
            Note note = noteService.getNoteById(UUID.fromString(id));
            User user = userService.getCurrentUser();

            if (!user.getAdminFlag()
                    && (note == null || note.getOwner() == null || !note.getOwner().equals(applicationUser()))) {
                return "redirect:/note/all";
            }

            note.setEditDateTime(LocalDateTime.now());
            NoteDto noteDto = noteService.convertToDto(note);
            model.addAttribute("noteDto", noteDto);

            return "edit";
        } catch (Exception e) {
            return "redirect:/note/create";
        }
    }


    /**
     * Обработка запроса на сохранение объекта.
     *
     * @param noteDto - объект, который содержит данные для сохранения. Объект неполноценный (не все поля).
     * @return возвращает адрес (относительный путь), куда надо идти клиенту.
     */
    @PostMapping("/save")
    @PreAuthorize("isAuthenticated()")
    public String saveNote(NoteDto noteDto) {
        try {
            if (noteDto.getOwner() == null) {
                if (applicationUser() != null) {
                    noteService.saveNote(noteDto, applicationUser());
                } else {
                    return "redirect:/note/create";
                }
            } else {

                //Если заметка отредактирована админом, внизу указывается информация и дата редактирования.
                if (applicationUser().getAdminFlag()) {
                    LocalDateTime date = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String formatedDate = date.format(formatter);
                    noteDto.setText(noteDto.getText() + "\n\n\nEdited by admin " + formatedDate);
                }

                noteService.saveNote(noteDto, noteDto.getOwner());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/note/all";
    }


    /**
     * Удаление заметки.
     *
     * @param id идентификатор заметки для удаления в виде строки
     * @return шаблон Thymeleaf для отображения результатов
     */
    @GetMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public String delete(@RequestParam("id") String id) {
        Note note = noteService.getNoteById(id);

        // проверяем, что пользователю можно удалять заметку
        if (note != null && note.getOwner() != null && note.getOwner().equals(applicationUser())) {
            noteService.deleteNoteById(UUID.fromString(id));
        }
        return "redirect:/note/all";
    }


    /**
     * Метод добавляет в модель каждого метода текущего пользователя.
     *
     * @return возвращаем пользователя приложения
     */
    @ModelAttribute("user")
    public User applicationUser() {
        return userService.getCurrentUser();
    }
}
