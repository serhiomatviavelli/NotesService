package ru.sberbank.jd.notesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс spring приложения "Сервис заметок".
 * Стартовая точка здесь, но вся логика в других классах.
 */
@SpringBootApplication
public class NotesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesServiceApplication.class, args);
    }

}
