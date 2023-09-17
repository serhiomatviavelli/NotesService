package ru.sberbank.jd.notesservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер, призванный обработать обращение к корневому элементу сайта.
 */
@Controller
public class DefaultController {

    @GetMapping("/")
    public String rootPage() {
        return "redirect:note/all";
    }
}
