package ru.sberbank.jd.notesservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.service.UserService;

/**
 * Контроллер Security.
 */
@AllArgsConstructor
@Controller
@RequestMapping(value = "/auth")
public class UserController {

    private UserService service;

    /**
     * Отображение формы регистрации пользователя.
     *
     * @param model передает объект User в UI.
     * @return шаблон для регистрации пользователя.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "registration";
    }

    /**
     * Обрабатывает добавление пользователя.
     *
     * @param model для передачи ошибки в интерфейс
     * @param user объект User
     * @return возвращает страницу для отображения: если успешно /note/all, если ошибка /register.
     */
    @PostMapping("/register")
    public String saveUser(Model model, User user)  {
        boolean isError = service.addUser(user);
        if (isError) {
            model.addAttribute("err", isError);
            return "registration";
        }
        return "redirect:/note/all";
    }

}
