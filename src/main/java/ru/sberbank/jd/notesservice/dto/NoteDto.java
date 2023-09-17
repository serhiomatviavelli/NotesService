package ru.sberbank.jd.notesservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sberbank.jd.notesservice.dao.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс для отображения на клиенте.
 * В частности отображает Markdown в виде форматированного текста.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class NoteDto {

    private UUID id;
    private String title;
    private String text;
    private User owner;
    private Boolean publicFlag;
    private String formattedText;
    private LocalDateTime createDateTime;
    private LocalDateTime editDateTime;
    private String tagString;
}
